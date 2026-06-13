package by.krainet.auth.Util;


import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    public String extract(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    public String extractOrNull(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }
}
