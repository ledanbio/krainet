package by.krainet.common.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private String username;
    private String email;
    private String rawPassword;

    @Override
    public String toString(){
        return "пользователь с именем - " + username + ",паролем - "+ rawPassword + " и почтой - " + email;
    }
}
