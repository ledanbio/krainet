package by.krainet.auth.Service;


import by.krainet.auth.Entity.User;
import by.krainet.auth.Exception.UserNotFoundException;
import by.krainet.auth.Repository.AuthRepo;
import by.krainet.auth.Service.kafka.AuthEventProducer;
import by.krainet.common.dto.PasswordChangeRequest;
import by.krainet.common.dto.UserDataResponse;
import by.krainet.common.dto.UserUpdateRequest;
import by.krainet.common.event.UserDeleteEvent;
import by.krainet.common.event.UserUpdateEvent;
import by.krainet.common.event.UserUpdatePasswordEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final AuthRepo authRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthEventProducer eventProducer;



    public UserDataResponse getMe(){
        User user = getUser();

        log.debug("GetMe successful");
        return UserDataResponse.builder()
                 .username(user.getUsername())
                 .lastName(user.getLastName())
                 .firstName(user.getFirstName())
                 .email(user.getEmail())
                 .build();

    }

    @Transactional
    public UserDataResponse updateUserData(UserUpdateRequest request) {
        User user = getUser();

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
            PasswordChangeRequest request
    ){
        User user = getUser();

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        log.info("updateCurrentUserPassword Success: password changed");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));


        authRepo.save(user);
        eventProducer.sendUserUpdatePasswordEvent(
                UserUpdatePasswordEvent.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .rawOldPassword(request.getOldPassword())
                        .rawNewPassword(request.getNewPassword())
                        .build()
        );
    }


    @Transactional
    public void deleteCurrentUser() {
        User user = getUser();

        eventProducer.sendUserDeleteEvent(
                UserDeleteEvent.builder()
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .build()
        );

        log.info("deleteCurrentUser Success: user deleted");
        authRepo.delete(user);
    }

    private User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            log.error("getCurrentUser failed: no authentication in context");
            throw new IllegalStateException("User not authenticated");
        }
        Long userId;
        try {
            userId = (Long) auth.getPrincipal();
        } catch (ClassCastException e) {
            log.error("getCurrentUser failed: principal is not Long, type={}",
                    auth.getPrincipal().getClass().getName());
            throw new IllegalStateException("Invalid authentication principal");
        }
        return authRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

}
