package by.krainet.auth.Exception;


import java.net.URI;

public final class ErrorTypes {
    private static final String BASE = "https://krainet.by/api/v1/errors";

    public static final URI USER_NOT_FOUND = URI.create(BASE + "user-not-found");
    public static final URI INVALID_CREDENTIALS = URI.create(BASE + "invalid-credentials");
    public static final URI USER_ALREADY_EXISTS = URI.create(BASE + "user-already-exists");
    public static final URI ACCESS_DENIED = URI.create(BASE + "access-denied");
    public static final URI VALIDATION_ERROR = URI.create(BASE + "validation-error");
    public static final URI INTERNAL_ERROR = URI.create(BASE + "internal-error");
    public static final URI TOKEN_EXPIRED = URI.create(BASE + "token-expired");
    public static final URI TOKEN_INVALID = URI.create(BASE + "token-invalid");
    public static final URI USER_ALREADY_ADMIN = URI.create(BASE + "user-already-admin");

    private ErrorTypes() {}
}
