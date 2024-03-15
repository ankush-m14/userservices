package scaler.com.userservices.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scaler.com.userservices.dtos.*;
import scaler.com.userservices.exceptions.NotFoundException;
import scaler.com.userservices.exceptions.UserAlreadyExistException;
import scaler.com.userservices.models.SessionStatus;
import scaler.com.userservices.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) throws NotFoundException {
        return authService.logIn(request.getEmail(), request.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) throws NotFoundException {
        authService.logout(request.getToken(), request.getUserId());
        return ResponseEntity.ok().build();
    }

    //In signUp we create new User for that frontend send me email & password
    //not worried about to read password because we are using https s:belongs to secure data will encrypted
     @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request) throws UserAlreadyExistException {
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    public ResponseEntity<SessionStatus> validate(@RequestBody ValidateTokenRequestDto request){
        SessionStatus sessionStatus = authService.validate(request.getToken(), request.getUserId());
        return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
    }
}
