package by.krainet.auth.Handler;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/problem+json");

        String json = """
            {
                "type": "https://krainet.by/api/v1/errors/access-denied",
                "title": "Access denied",
                "status": 403,
                "detail": "You don't have permission to access this resource.",
                "instance": "%s",
                "timestamp": "%s"
            }
            """.formatted(request.getRequestURI(), java.time.Instant.now());

        response.getWriter().write(json);
    }
}