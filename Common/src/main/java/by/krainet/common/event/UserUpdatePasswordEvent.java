package by.krainet.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePasswordEvent {
    private String username;
    private String email;
    private String rawOldPassword;
    private String rawNewPassword;

}