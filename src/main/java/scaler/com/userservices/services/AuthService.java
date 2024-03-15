package scaler.com.userservices.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import scaler.com.userservices.dtos.UserDto;
import scaler.com.userservices.exceptions.NotFoundException;
import scaler.com.userservices.exceptions.UserAlreadyExistException;
import scaler.com.userservices.models.Session;
import scaler.com.userservices.models.SessionStatus;
import scaler.com.userservices.models.User;
import scaler.com.userservices.repositeries.SessionRepository;
import scaler.com.userservices.repositeries.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private SecretKey secretKey;

    @Autowired
    public AuthService(UserRepository userRepository, SessionRepository sessionRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        secretKey = Jwts.SIG.HS256.key().build();
    }

    public UserDto signUp(String email, String password) throws UserAlreadyExistException {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent()){
            throw new UserAlreadyExistException("The User with " + email + " already Exists.");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }

    public ResponseEntity<UserDto> logIn(String email, String password) throws NotFoundException {
         Optional<User> userOptional = userRepository.findByEmail(email);

         if(userOptional.isEmpty()){
             throw new NotFoundException("The User is Not Found. Can you please SignUp");
         }

        User user = userOptional.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("This password is incorrect");
        }

        // String token = RandomStringUtils.randomAlphanumeric(30);

        Map<String, Object> jwtData = new HashMap<>();
        jwtData.put("email", email);
        jwtData.put("created_at", java.sql.Date.valueOf(LocalDate.now()));
        jwtData.put("expiry_at", java.sql.Date.valueOf(LocalDate.now().plusDays(3)));

        // SecretKey secretKey = Jwts.SIG.HS256.key().build();
        // [if we use the secret key
        //  in login method then it problem to use in validate method
        //  (not accessible to  outside method) becoz of this we can use this
        //  key global level variable]

         String token = Jwts.builder()
                       .claims(jwtData)
                       .signWith(secretKey)
                       .compact();

         Session session = new Session();
         session.setSessionStatus(SessionStatus.ACTIVE);
         session.setToken(token);
         session.setUser(user);
         LocalDate currentDate = LocalDate.now();
         session.setExpiryDate(currentDate.plusDays(3));
         session.setSessionStatus(SessionStatus.ACTIVE);

         sessionRepository.save(session);

         UserDto userDto = UserDto.from(user);

        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);

        return new ResponseEntity<UserDto>(userDto, headers, HttpStatus.OK);
    }

    public void logout(String token, Long userId) throws NotFoundException{
        Optional<Session> sessionOptional = sessionRepository
                .findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            throw new NotFoundException("The token is invalid or the details are mismatching");
        }

        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
    }

    public SessionStatus validate(String token, Long userId){
        Optional<Session> sessionOptional = sessionRepository
                .findByTokenAndUser_Id(token, userId);

        if(sessionOptional.isEmpty()) {
            return SessionStatus.ENDED;
        }
        Session session = sessionOptional.get();

//        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
//            return SessionStatus.ENDED;
//        }
        if(session.getExpiryDate().isBefore(LocalDate.now())
                || session.getSessionStatus() != SessionStatus.ACTIVE) {
            return SessionStatus.ENDED;
        }
        Jws<Claims> claimsJws = Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        String email = (String) claimsJws.getPayload().get("email");
        Long expiryAt = (Long) claimsJws.getPayload().get("expiry_at");
        System.out.println("Email " + email);
        System.out.println("Expiry At " + expiryAt);

        return SessionStatus.ACTIVE;
    }

}
