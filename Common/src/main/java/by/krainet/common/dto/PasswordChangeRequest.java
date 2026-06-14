package by.krainet.common.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на смену пароля")
public class PasswordChangeRequest {
    @Schema(description = "Текущий пароль", example = "my_1secret1_password")
    @NotBlank
    String oldPassword;

    @Schema(description = "Новый пароль", example = "stronger_password123*")
    @NotBlank
    @Size(min = 8, message = "Пароль минимум 8 символов")
    String newPassword;
}
