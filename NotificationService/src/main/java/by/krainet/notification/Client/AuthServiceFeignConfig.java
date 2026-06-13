package by.krainet.notification.Client;


import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AuthServiceFeignConfig {

    @Value("${auth.service.api-key}")
    private String apiKey;


    @Bean
    public RequestInterceptor serviceApiKeyInterceptor(){
        return requestTemplate -> {
            requestTemplate.header("X-Internal-Api-Key", apiKey);
        };
    }


}
