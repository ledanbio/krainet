package by.krainet.auth.Service;


import by.krainet.auth.Entity.User;
import by.krainet.auth.Exception.UserNotFoundException;
import by.krainet.auth.Repository.AuthRepo;
import by.krainet.common.dto.AdminEmailResponse;
import by.krainet.common.dto.UserDataResponse;
import by.krainet.common.dto.UserUpdateRequest;
import by.krainet.common.enums.ROLE;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final AuthRepo authRepo;
    private final PasswordEncoder passwordEncoder;


    public UserDataResponse getUserByUserId(Long id) {
        User user = authRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("getUserByUserId Failed: User {} not found", id);
                    return new UserNotFoundException(id);
                });

        return UserDataResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }


    @Transactional
    public UserDataResponse updateDataByUserId(Long id, UserUpdateRequest request) {
        User user = authRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        boolean haveChanges = false;

        if (!Objects.equals(user.getUsername(), request.getUsername())){
            if (authRepo.existsByUsername(request.getUsername())) {
                log.warn("updateDataByUserId Failed: Username {} already taken", request.getUsername());
                throw new IllegalArgumentException("Username already taken: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
            haveChanges = true;
        }
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            if (authRepo.existsByEmail(request.getEmail())){
                log.warn("updateDataByUserId Failed: Email {} already registered", request.getEmail());
                throw new IllegalArgumentException("Email already registered: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
            haveChanges = true;
        }
        if (!Objects.equals(user.getFirstName(), request.getFirstName())) {
            user.setFirstName(request.getFirstName());
            haveChanges = true;
        }
        if (!Objects.equals(user.getLastName(), request.getLastName())){
            user.setLastName(request.getLastName());
            haveChanges = true;
        }

        if (haveChanges){
            log.info("updateDataByUserId Success: User data changed");
            user = authRepo.save(user);
        }
        else {
            log.info("updateDataByUserId : Nothing to change");
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
    public void deleteUserByUserId(Long id) {
        User user = authRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        authRepo.delete(user);
        log.info("deleteUserByUserId Success: User deleted");
    }

    @Transactional
    public void updateUserPasswordByUserId(Long id, String newPassword) {
        User user = authRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setPassword(passwordEncoder.encode(newPassword));

        authRepo.save(user);
        log.info("updateUserPasswordByUserId Success: User password changed");
    }

    public AdminEmailResponse getAdminEmails() {
        List<String> emails = authRepo.findAllByRole(ROLE.ADMIN)
                .stream()
                .map(User::getEmail)
                .toList();

        log.debug("getAdminEmails Success");
        return AdminEmailResponse.builder()
                .emails(emails)
                .build();
    }
}
