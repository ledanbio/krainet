package by.krainet.common.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name = "Запрос на изменение данных пользователя")
public class UserUpdateRequest {
    @Schema(description = "Новое имя пользователя", example = "ledanbio")
    @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Новый адрес электронной почты", example = "ledanbio@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;


    @Schema(description = "Новое имя", example = "Ilya")
    @Size(min = 2, max = 50, message = "Имя должно быть от двух до 50 букв")
    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @Schema(description = "Новая фамилия", example = "Smiths")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от двух до 50 букв")
    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;
}
