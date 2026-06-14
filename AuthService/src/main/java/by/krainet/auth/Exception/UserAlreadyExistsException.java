package by.krainet.auth.Exception;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String login){
        super("User with login: " + login + " already exists");
    }
}
