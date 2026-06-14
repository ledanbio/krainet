package by.krainet.auth.Exception;

public class TokenInvalidException extends RuntimeException {
    public TokenInvalidException(String reason) {
        super("Token invalid: " + reason);
    }
}
