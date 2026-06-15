package by.krainet.common.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Builder
@Schema(name = "Данные пользователя")
public class UserDataResponse {
    @Nullable
    @Schema(description = "Айди пользователя(Для админа)", example = "42",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(description = "Имя пользователя", example = "ledanbio")
    private String username;

    @Schema(description = "Адрес электронной почты", example = "ledanbio@gmail.com")
    private String email;

    @Schema(description = "Имя", example = "Ilya")
    private String firstName;

    @Schema(description = "Фамилия", example = "Smiths")
    private String lastName;
}
