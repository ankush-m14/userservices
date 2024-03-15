package scaler.com.userservices.services;

import org.springframework.stereotype.Service;
import scaler.com.userservices.controllers.UserController;
import scaler.com.userservices.dtos.UserDto;
import scaler.com.userservices.models.Role;
import scaler.com.userservices.models.User;
import scaler.com.userservices.repositeries.RoleRepository;
import scaler.com.userservices.repositeries.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
      private RoleRepository roleRepository;
     private UserRepository userRepository;

    public UserService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public UserDto getUserDetails(Long userId) {
        return new UserDto(); // returning an empty user for now. Update this to fetch user details from the DB
    }

    public UserDto setUserRoles(Long userId, List<Long> roleIds) {
        Optional<User> userOptional = userRepository.findById(userId);
        List<Role> roles = roleRepository.findAllByIdIn(roleIds);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        user.setRoles(Set.copyOf(roles));

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }
}
