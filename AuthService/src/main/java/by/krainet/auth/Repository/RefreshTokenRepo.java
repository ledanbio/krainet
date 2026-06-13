package by.krainet.auth.Repository;

import by.krainet.auth.Entity.RefreshToken;
import by.krainet.auth.Entity.User;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    boolean existsByUserId(Long id);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByToken(String token);

    void deleteAllByExpiresAtBefore(Instant now);
}
