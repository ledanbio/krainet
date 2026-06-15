package by.krainet.auth.Util;


import by.krainet.auth.Entity.User;
import by.krainet.auth.Repository.AuthRepo;
import by.krainet.common.enums.ROLE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {
    private final AuthRepo authRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.username}")
    private String adminUsername;

    @Value("${admin.default.email:admin@krainet.by}")
    private String adminEmail;

    @Value("${admin.default.password:}")
    private String adminPassword;

    @Override
    public void run(String... args){
        if(authRepo.count() == 0){
            if (adminPassword.isBlank()){
                String generatedPassword = generateRandomPassword();
                log.warn("========================================");
                log.warn("GENERATED ADMIN PASSWORD: {}", generatedPassword);
                log.warn("CHANGE THIS PASSWORD IMMEDIATELY!");
                log.warn("========================================");
                adminPassword = generatedPassword;
            }
            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .firstName("System")
                    .lastName("Administrator")
                    .role(ROLE.ADMIN)
                    .build();

            authRepo.save(admin);
            log.info("System admin created: {}", adminUsername);
        }
    }

    private String generateRandomPassword() {
        return java.util.UUID.randomUUID().toString().substring(0, 16);
    }
}
