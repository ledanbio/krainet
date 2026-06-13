package by.krainet.auth.service;

import by.krainet.auth.Util.TokenExtractor;
import by.krainet.auth.Entity.RefreshToken;
import by.krainet.auth.Entity.User;
import by.krainet.auth.Repository.RefreshTokenRepo;
import by.krainet.auth.Service.JwtService;
import by.krainet.common.dto.TokenResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepository;
    private final JwtService jwtService;
    private final TokenExtractor tokenExtractor;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        String token = jwtService.generateRefreshToken(user.getId());
        Instant expiresAt = jwtService.extractExpiration(token);

        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .expiresAt(expiresAt)
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public TokenResponse refresh(String rawToken) {
        String refreshTokenString = tokenExtractor.extract(rawToken);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new EntityNotFoundException("Refresh token not found"));

        if (isTokenExpired(refreshToken)) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        if (!jwtService.isTokenValid(refreshTokenString)) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Invalid refresh token");
        }

        User user = refreshToken.getUser();

        refreshTokenRepository.delete(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        RefreshToken newRefreshToken = createRefreshToken(user);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .expiresIn(jwtService.getAccessExpirationSeconds())
                .tokenType("Bearer")
                .build();
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("findByToken Failed: Refresh token not found");
                    return new RuntimeException("Refresh token not found");
                });
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiresAt().isBefore(Instant.now());
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteAllByExpiresAtBefore(Instant.now());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        deleteExpiredTokens();
    }
}