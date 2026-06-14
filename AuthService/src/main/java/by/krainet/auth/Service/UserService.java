package by.krainet.auth.Service;


import by.krainet.auth.Exception.UserNotFoundException;
import by.krainet.auth.Util.TokenExtractor;
import by.krainet.auth.Entity.User;
import by.krainet.auth.Repository.AuthRepo;
import by.krainet.auth.Service.kafka.AuthEventProducer;
import by.krainet.common.dto.PasswordChangeRequest;
import by.krainet.common.dto.UserDataResponse;
import by.krainet.common.dto.UserUpdateRequest;
import by.krainet.common.event.UserDeleteEvent;
import by.krainet.common.event.UserUpdateEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final AuthRepo authRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthEventProducer eventProducer;
    private final TokenExtractor tokenExtractor;


    public UserDataResponse getMe(String rawToken){
        User user = findUserByToken(rawToken);

        log.debug("GetMe successful");
        return UserDataResponse.builder()
                 .username(user.getUsername())
                 .lastName(user.getLastName())
                 .firstName(user.getFirstName())
                 .email(user.getEmail())
                 .build();

    }

    @Transactional
    public UserDataResponse updateUserData(String rawToken, UserUpdateRequest request) {
        User user = findUserByToken(rawToken);

        boolean haveChanges = false;
        String changes = "";
        String oldUsername = user.getUsername();
        String oldEmail = user.getEmail();

        if (!Objects.equals(user.getUsername(), request.getUsername())){
            if (authRepo.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already taken: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
            changes+=("новое имя пользователя - "+ request.getUsername() + " ");
            haveChanges = true;
        }
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            if (authRepo.existsByEmail(request.getEmail())){
                throw new IllegalArgumentException("Email already registered: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
            changes+=("новый адрес электронной почты - "+ request.getEmail() + " ");
            haveChanges = true;
        }
        if (!Objects.equals(user.getFirstName(), request.getFirstName())) {
            user.setFirstName(request.getFirstName());
            changes+=("новое Имя - "+ request.getFirstName() + " ");
            haveChanges = true;
        }
        if (!Objects.equals(user.getLastName(), request.getLastName())){
            user.setLastName(request.getLastName());
            changes+=("новое Фамилия - "+ request.getLastName() + " ");
            haveChanges = true;
        }

        if (haveChanges){
            log.info("updateUserData: Successful updated user data");
            user = authRepo.save(user);
            eventProducer.sendUserUpdateEvent(
                    UserUpdateEvent.builder()
                            .username(oldUsername)
                            .email(oldEmail)
                            .changes(changes)
                            .build()
            );
        }
        else {
            log.info("updateUserData: Nothing to change");
             return UserDataResponse.builder()
                     .username(user.getUsername())
                     .email(user.getEmail())
                     .firstName(user.getFirstName())
                     .lastName(user.getLastName())
                     .build();
        }
        return UserDataResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    @Transactional
    public void updateCurrentUserPassword(
            String rawToken, PasswordChangeRequest request
    ){
        User user = findUserByToken(rawToken);

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        log.info("updateCurrentUserPassword Success: password changed");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        authRepo.save(user);
    }


    @Transactional
    public void deleteCurrentUser(String rawToken) {
        User user = findUserByToken(rawToken);

        eventProducer.sendUserDeleteEvent(
                UserDeleteEvent.builder()
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .build()
        );

        log.info("deleteCurrentUser Success: user deleted");
        authRepo.delete(user);
    }

    private User findUserByToken(String rawToken){
        String token = tokenExtractor.extract(rawToken);

        Long userId = jwtService.extractUserId(token);
        return authRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
