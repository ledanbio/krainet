package by.krainet.notification.Client;

import by.krainet.common.dto.AdminEmailResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthServiceClientFallback implements AuthServiceClient{
    @Override
    public AdminEmailResponse getAdminEmails(){
        return AdminEmailResponse.builder()
                .emails(List.of())
                .build();
    }
}
