package by.krainet.auth.Service.kafka;


import by.krainet.common.event.UserDeleteEvent;
import by.krainet.common.event.UserRegisteredEvent;
import by.krainet.common.event.UserUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthEventProducer {
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaRegisteredTemplate;
    private final KafkaTemplate<String, UserUpdateEvent> kafkaUpdatedTemplate;
    private final KafkaTemplate<String, UserDeleteEvent> kafkaDeletedTemplate;

    public void sendUserRegisteredEvent(UserRegisteredEvent userRegisteredEvent){
        kafkaRegisteredTemplate.send("user-registered", userRegisteredEvent.getUsername().toString(), userRegisteredEvent);
    }

    public void sendUserUpdateEvent(UserUpdateEvent userUpdateEvent){
        kafkaUpdatedTemplate.send("user-updated", userUpdateEvent.getUsername().toString(), userUpdateEvent);
    }

    public void sendUserDeleteEvent(UserDeleteEvent userDeleteEvent){
        kafkaDeletedTemplate.send("user-deleted", userDeleteEvent.getUsername().toString(), userDeleteEvent);
    }
}
