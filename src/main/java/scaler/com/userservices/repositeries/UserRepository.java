package scaler.com.userservices.repositeries;

import org.springframework.data.jpa.repository.JpaRepository;
import scaler.com.userservices.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
