package by.krainet.notification.Client;


import by.krainet.common.dto.AdminEmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "auth-service",
        url = "${auth.service.url:http://auth-service:8080}",
        configuration = AuthServiceFeignConfig.class,
        fallback = AuthServiceClientFallback.class
)
public interface AuthServiceClient {

    @GetMapping("/api/v1/admin/emails")
    AdminEmailResponse getAdminEmails();
}
