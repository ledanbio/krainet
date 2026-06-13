package by.krainet.auth.Service;

import by.krainet.auth.Util.TokenExtractor;
import by.krainet.auth.Service.kafka.AuthEventProducer;
import by.krainet.auth.service.RefreshTokenService;
import by.krainet.auth.Entity.User;
import by.krainet.auth.Repository.AuthRepo;
import by.krainet.common.dto.SignInRequest;
import by.krainet.common.dto.SignUpRequest;
import by.krainet.common.dto.TokenResponse;
import by.krainet.common.enums.ROLE;
import by.krainet.common.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthRepo authRepo;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthEventProducer eventProducer;
    private final TokenExtractor tokenExtractor;

    @Transactional
    public TokenResponse register(SignUpRequest request) {
        if (authRepo.existsByUsername(request.getUsername())) {
            log.warn("User already taken");
            throw new IllegalArgumentException("User already taken: " + request.getUsername());
        }
        if (authRepo.existsByEmail(request.getEmail())) {
            log.warn("Email already registered");
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(ROLE.USER)
                .build();

        log.info("User {} registered", newUser.getUsername());

        eventProducer.sendUserRegisteredEvent(
                UserRegisteredEvent.builder()
                        .email(newUser.getEmail())
                        .username(newUser.getUsername())
                        .rawPassword(request.getPassword())
                        .build()
        );

        authRepo.save(newUser);
        log.info("User created with id - {}", newUser.getId());
        String refresh = refreshTokenService.createRefreshToken(newUser).getToken();

        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(newUser.getId(), ROLE.USER))
                .refreshToken(refresh)
                .expiresIn(jwtService.getAccessExpirationSeconds())
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public TokenResponse login(SignInRequest request) {
        String login = request.getLogin();

        User user;
        if (login.contains("@")) {
            user = authRepo.findByEmail(login)
                    .orElseThrow(() -> {
                        log.warn("Login failed: user not found ");
                        return new BadCredentialsException("Invalid credentials");
                    });
        } else {
            user = authRepo.findByUsername(login)
                    .orElseThrow(() -> {
                        log.warn("Login failed: user not found ");
                        return new BadCredentialsException("Invalid credentials");
                    });
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: invalid password {}", login);
            throw new BadCredentialsException("Invalid credentials");
        }

        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(user.getId(), user.getRole()))
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessExpirationSeconds())
                .tokenType("Bearer")
                .build();
    }

    @Transactional
    public void logout(String rawToken) {
        String token = tokenExtractor.extract(rawToken);
        if (refreshTokenService.findByToken(token) == null) {
            log.warn("Logout failed: session not found");
            throw new IllegalArgumentException("Session not found");
        }

        log.debug("Logout: {}", refreshTokenService.findByToken(token).getUser().getUsername());
        refreshTokenService.deleteByToken(token);
    }
}