package by.krainet.auth.Handler;

import by.krainet.auth.Exception.*;
import by.krainet.common.dto.exception.ProblemDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExists(
            UserAlreadyExistsException e,
            HttpServletRequest request) {
        log.warn(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ProblemDetail.builder()
                        .type(ErrorTypes.USER_ALREADY_EXISTS)
                        .title("User already exists")
                        .status(409)
                        .detail(e.getMessage())
                        .instance(URI.create(request.getRequestURI()))
                        .timestamp(Instant.now())
                        .build());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFound(
            UserNotFoundException e,
            HttpServletRequest request) {
        log.warn(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.builder()
                        .type(ErrorTypes.USER_NOT_FOUND)
                        .title("User not found")
                        .status(404)
                        .detail(e.getMessage())
                        .instance(URI.create(request.getRequestURI()))
                        .timestamp(Instant.now())
                        .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(
            BadCredentialsException e,
            HttpServletRequest request) {
        log.warn("Login failed: Invalid credentials");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ProblemDetail.builder()
                        .type(ErrorTypes.INVALID_CREDENTIALS)
                        .title("Invalid credentials")
                        .status(401)
                        .detail("Login or password is incorrect")
                        .instance(URI.create(request.getRequestURI()))
                        .timestamp(Instant.now())
                        .build());
    }


    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ProblemDetail> handleTokenExpired(
            TokenExpiredException e,
            HttpServletRequest request) {
        log.warn("Token expired: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ProblemDetail.builder()
                        .type(ErrorTypes.TOKEN_EXPIRED)
                        .title("Token expired")
                        .status(401)
                        .detail(e.getMessage())
                        .instance(URI.create(request.getRequestURI()))
                        .timestamp(Instant.now())
                        .build());
    }


    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ProblemDetail> handleTokenInvalid(
            TokenInvalidException e,
            HttpServletRequest request) {
        log.warn("Token invalid: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ProblemDetail.builder()
                        .type(ErrorTypes.TOKEN_INVALID)
                        .title("Token invalid")
                        .status(401)
                        .detail(e.getMessage())
                        .instance(URI.create(request.getRequestURI()))
                        .timestamp(Instant.now())
                        .build());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ProblemDetail> handleInvalidToken(
            InvalidTokenException e,
            HttpServletRequest request) {
        log.warn("Invalid token: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ProblemDetail.builder()
                        .type(ErrorTypes.TOKEN_INVALID)
                        .title("Authentication failed")
                        .status(401)
                        .detail(e.getMessage())
                        .instance(URI.create(request.getRequestURI()))
                        .timestamp(Instant.now())
                        .build());
    }




    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AccessDeniedException e,
            HttpServletRequest request) {
        log.warn("Access denied to {}", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ProblemDetail.builder()
                        .type(ErrorTypes.ACCESS_DENIED)
                        .title("Access denied")
                        .status(403)
                        .detail("You don't have permission to access this resource")
                        .instance(URI.create(request.getRequestURI()))
                        .timestamp(Instant.now())
                        .build());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", details);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.builder()
                        .type(ErrorTypes.VALIDATION_ERROR)
                        .title("Validation failed")
                        .status(400)
                        .detail(details)
                        .instance(URI.create(request.getRequestURI()))
                        .timestamp(Instant.now())
                        .build());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException e,
            HttpServletRequest request) {
        String details = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));

        log.warn("Constraint violation: {}", details);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.builder()
                        .type(ErrorTypes.VALIDATION_ERROR)
                        .title("Constraint violation")
                        .status(400)
                        .detail(details)
                        .instance(URI.create(request.getRequestURI()))
                        .timestamp(Instant.now())
                        .build());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(
            Exception e,
            HttpServletRequest request) {
        log.error("Unexpected error: {}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.builder()
                        .type(ErrorTypes.INTERNAL_ERROR)
                        .title("Internal server error")
                        .status(500)
                        .detail("Something went wrong. Please try again later.")
                        .instance(URI.create(request.getRequestURI()))
                        .timestamp(Instant.now())
                        .build());
    }
}