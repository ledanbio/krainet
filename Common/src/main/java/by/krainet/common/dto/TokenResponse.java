package by.krainet.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.DateTimeException;
import java.util.Date;

@Data
@Builder
@Schema(name = "Ответ регистрации")
public class TokenResponse {

    @Schema(description = "Токен доступа", example = "")
    private String accessToken;

    @Schema(description = "Токен обновления", example = "")
    private String refreshToken;

    @Schema(description = "Истечение токена в секундах", example = "900")
    private Long expiresIn;

    @Schema(description = "Тип токена", example = "Bearer")
    private String tokenType;

}