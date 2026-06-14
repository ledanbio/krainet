package by.krainet.auth.Handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/problem+json");

        String json = """
            {
                "type": "https://krainet.by/api/v1/errors/not-authenticated",
                "title": "Not authenticated",
                "status": 401,
                "detail": "Authentication required. Please provide a valid JWT token.",
                "instance": "%s",
                "timestamp": "%s"
            }
            """.formatted(request.getRequestURI(), java.time.Instant.now());

        response.getWriter().write(json);
    }
}