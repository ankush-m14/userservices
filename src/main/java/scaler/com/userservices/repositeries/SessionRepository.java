package scaler.com.userservices.repositeries;

import org.springframework.data.jpa.repository.JpaRepository;
import scaler.com.userservices.models.Session;

import java.util.Optional;

public interface SessionRepository  extends JpaRepository<Session, Long> {
    Optional<Session> findByTokenAndUser_Id(String token, Long userId);
}
