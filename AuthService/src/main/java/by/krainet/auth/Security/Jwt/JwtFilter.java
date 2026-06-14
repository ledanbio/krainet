package by.krainet.auth.Security.Jwt;

import by.krainet.auth.Exception.TokenExpiredException;
import by.krainet.auth.Exception.TokenInvalidException;
import by.krainet.auth.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            jwtService.validateToken(jwt);

            Long userId = jwtService.extractUserId(jwt);
            String role = jwtService.extractRole(jwt).name();

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (TokenExpiredException e) {
            log.warn("JWT expired: uri={}, tokenPrefix={}",
                    request.getRequestURI(), jwt.substring(0, Math.min(20, jwt.length())));
            writeProblemDetail(response, 401, "token-expired", "Token has expired",
                    request.getRequestURI());

        } catch (TokenInvalidException e) {
            log.warn("JWT invalid: uri={}, reason={}", request.getRequestURI(), e.getMessage());
            writeProblemDetail(response, 401, "token-invalid", e.getMessage(),
                    request.getRequestURI());

        } catch (Exception e) {
            log.error("JWT unexpected error: uri={}, error={}", request.getRequestURI(), e.getMessage());
            writeProblemDetail(response, 401, "token-error", "Authentication failed",
                    request.getRequestURI());
        }
    }

    private void writeProblemDetail(HttpServletResponse response, int status,
                                    String type, String detail, String instance) throws IOException {
        response.setStatus(status);
        response.setContentType("application/problem+json");

        String json = String.format(
                "{\"type\":\"https://krainet.by/api/v1/errors/%s\"," +
                        "\"title\":\"%s\"," +
                        "\"status\":%d," +
                        "\"detail\":\"%s\"," +
                        "\"instance\":\"%s\"," +
                        "\"timestamp\":\"%s\"}",
                type,
                type.replace("-", " "),
                status,
                detail,
                instance,
                Instant.now()
        );

        response.getWriter().write(json);
    }
}