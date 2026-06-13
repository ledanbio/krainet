package by.krainet.notification.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendAdminNotificationRegistered(String adminEmail, String email, String username, String rawPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("Регистрация пользователя " + username);
        message.setText(String.format(
                "Создан пользователь с именем - %s" + ", паролем - %s"+ " и почтой - %s",
                username, rawPassword, email
        ));

        mailSender.send(message);
        log.info("sendAdminNotificationRegistered Success: mail send to {}", adminEmail);
    }

    public void sendAdminNotificationUpdated(String adminEmail, String email, String username, String changes) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("Изменение данных пользователя " + username);
        message.setText(String.format(
                "Изменен пользователь с именем - %s" + ", паролем - **********"+ " и почтой - %s\n"+ "Имеет следующие измениния - %s",
                username, email, changes
        ));

        mailSender.send(message);
        log.info("sendAdminNotificationUpdated Success: mail send to {}", adminEmail);
    }

    public void sendAdminNotificationDeleted(String adminEmail, String email, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("Удаление пользователя " + username);
        message.setText(String.format(
                "Удален пользователь с именем - %s" + ", паролем - ********** и почтой - %s",
                username, email
        ));

        mailSender.send(message);
        log.info("sendAdminNotificationDeleted Success: mail send to {}", adminEmail);
    }
}
