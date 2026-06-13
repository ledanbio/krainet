package by.krainet.notification.Service.kafka;


import by.krainet.common.dto.AdminEmailResponse;
import by.krainet.common.event.UserDeleteEvent;
import by.krainet.common.event.UserRegisteredEvent;
import by.krainet.common.event.UserUpdateEvent;
import by.krainet.notification.Client.AuthServiceClient;
import by.krainet.notification.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationEventConsumer {
    private final EmailService emailService;
    private final AuthServiceClient authClient;


    @KafkaListener(
            topics = "user-registered",
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserRegistered(UserRegisteredEvent event){
        AdminEmailResponse response = authClient.getAdminEmails();
        List<String> adminEmails = response.getEmails();

        if (adminEmails.isEmpty()){
            return;
        }

        for (String adminEmail : adminEmails){
            emailService.sendAdminNotificationRegistered(
                    adminEmail,
                    event.getEmail(),
                    event.getUsername(),
                    event.getRawPassword()
            );
        }
    }


    @KafkaListener(
            topics = "user-updated",
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserUpdate(UserUpdateEvent event){

        AdminEmailResponse response = authClient.getAdminEmails();
        List<String> adminEmails = response.getEmails();
        //TODO: Cashing admin emails
        if (adminEmails.isEmpty()){
            return;
        }

        for (String adminEmail : adminEmails){
            emailService.sendAdminNotificationUpdated(
                    adminEmail,
                    event.getEmail(),
                    event.getUsername(),
                    event.getChanges()
            );
        }
    }


    @KafkaListener(
            topics = "user-deleted",
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserDelete(UserDeleteEvent event){

        AdminEmailResponse response = authClient.getAdminEmails();
        List<String> adminEmails = response.getEmails();
        //TODO: Cashing admin emails
        if (adminEmails.isEmpty()){
            return;
        }

        for (String adminEmail : adminEmails){
            emailService.sendAdminNotificationDeleted(
                    adminEmail,
                    event.getEmail(),
                    event.getUsername()
            );
        }
    }
}
