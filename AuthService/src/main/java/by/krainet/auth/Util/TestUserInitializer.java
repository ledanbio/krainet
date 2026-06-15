package by.krainet.auth.Util;


import by.krainet.auth.Entity.User;
import by.krainet.auth.Repository.AuthRepo;
import by.krainet.common.enums.ROLE;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
@Order(2) // ← После админа
@RequiredArgsConstructor
@Slf4j
public class TestUserInitializer implements CommandLineRunner {

    private final AuthRepo authRepo;
    private final PasswordEncoder passwordEncoder;
    @Value("${test.data.enabled:true}")
    private boolean testDataEnabled;


    @Override
    public void run(String... args) {
        if(!testDataEnabled){
            return;
        }

        if (authRepo.count() > 1) {
            return;
        }

        String encodedPassword = passwordEncoder.encode("password123");
        List<String> safeDomains = List.of(
                "example.local", "test.invalid", "localhost.local",
                "domain.test", "mail.example", "fake.email",
                "dummy.local", "sample.invalid", "placeholder.test",
                "mock.local", "dev.null", "lab.test",
                "internal.local", "staging.invalid", "demo.test",
                "temp.local", "qa.email", "ci.invalid",
                "unit.test", "integration.local", "e2e.invalid",
                "load.test", "stress.local", "regression.invalid",
                "smoke.test", "sandbox.local", "preview.invalid",
                "canary.test", "bluegreen.local", "feature.invalid",
                "branch.test", "pr.local", "merge.invalid",
                "build.test", "deploy.local", "release.invalid",
                "hotfix.test", "patch.local", "version.invalid",
                "tag.test", "snapshot.local", "milestone.invalid",
                "sprint.test", "backlog.local", "story.invalid",
                "epic.test", "task.local", "bug.invalid",
                "issue.test", "ticket.local", "test.local",
                "user.invalid", "app.test"
        );

        List<User> users = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> {
                    String num = String.format("%03d", i);
                    return User.builder()
                            .username("Test_User_" + num)
                            .email("Test_User_" + num + "@" + safeDomains.get(i - 1))
                            .password(encodedPassword)
                            .firstName("First" + num)
                            .lastName("Last" + num)
                            .role(ROLE.USER)
                            .build();
                })
                .toList();

        authRepo.saveAll(users);
        log.debug("Inserted 50 test users");
    }
}