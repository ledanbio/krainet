package by.krainet.auth.Controller;

import by.krainet.auth.Exception.TokenExpiredException;
import by.krainet.auth.Service.AuthService;
import by.krainet.common.dto.*;
import by.krainet.auth.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Регистрация, аутентификация, обновление токена, выход")
public class AuthCont {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;




    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя. Возвращает токены"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь создан",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email или пароль не прошел валидацию",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(
            @RequestBody @Valid @Parameter(description = "Данные для регистрации", required = true)
            SignUpRequest request
    ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }





    @Operation(
            summary = "Вход в систему",
            description = "Осуществляет вход в систему. Возвращает токены"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный вход",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Пароль не прошел валидацию",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверные данные для входа",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login (
           @RequestBody @Valid @Parameter(description = "Данные для входа", required = true)
           SignInRequest request
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(request));
    }





    @Operation(
            summary = "Обновление токенов",
            description = "Обновляет токены. Возвращает токены"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Токен обновлен",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Токен устарел либо не валиден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestHeader("X-Refresh-Token") @Parameter(description = "Refresh токен для обновления access токена", required = true)
            String token){
        return ResponseEntity.ok(refreshTokenService.refresh(token));
    }






    @Operation(
            summary = "Выход пользователя из системы",
            description = "Осуществляет выход из системы"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Выход"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Токен устарел либо не валиден",
                    content = @Content(schema = @Schema(implementation = TokenExpiredException.class))
            )
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(
            @RequestHeader("X-Refresh-Token") @Parameter(description = "Refresh токен для выхода", required = true)
            String refreshToken
    ){
        authService.logout(refreshToken);
        SecurityContextHolder.clearContext();
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
