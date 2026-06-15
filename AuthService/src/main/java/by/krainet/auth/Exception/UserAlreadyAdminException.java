package by.krainet.auth.Exception;

public class UserAlreadyAdminException extends RuntimeException {
    public UserAlreadyAdminException(Long id) {
        super("User with id: " + id + " is already admin");
    }
}
