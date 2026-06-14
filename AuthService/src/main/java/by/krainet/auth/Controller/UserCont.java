package by.krainet.auth.Controller;


import by.krainet.auth.Service.UserService;
import by.krainet.common.dto.PasswordChangeRequest;
import by.krainet.common.dto.UserDataResponse;
import by.krainet.common.dto.UserUpdateRequest;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Получение данных пользователя, обновление данных текущего пользователя, обновление пароля текущего пользователя, удаление профиля пользователя")
public class UserCont {
    private final UserService userService;


    @Operation(
            summary = "Получение данных профиля"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные пользователя",
                    content = @Content(schema = @Schema(implementation = UserDataResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Токен устарел либо невалиден"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )

    })
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDataResponse> me(
            @RequestHeader("Authorization") @Valid @Parameter(name = "accessToken", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlIjoidXNlciIsImV4cCI6OTk5OTk5OTk5OX0.Qb4G6U7B8c9dE0fF1gH2iJ3kL4mN5oP6qR7sT8uV9wX0yZ")
            String token
    ){
        UserDataResponse response = userService.getMe(token);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }




    @Operation(
            summary = "Обновление данных профиля"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Обновленные данные пользователя",
                    content = @Content(schema = @Schema(implementation = UserDataResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Токен устарел либо невалиден"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDataResponse> updateUserData (
            @Parameter(name = "accessToken", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlIjoidXNlciIsImV4cCI6OTk5OTk5OTk5OX0.Qb4G6U7B8c9dE0fF1gH2iJ3kL4mN5oP6qR7sT8uV9wX0yZ")
            @RequestHeader("Authorization") String token,
            @Parameter(name = "Измененные данные пользователя", schema = @Schema(implementation = UserUpdateRequest.class))
            @RequestBody @Valid UserUpdateRequest request
    ){
        UserDataResponse response = userService.updateUserData(token, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }





    @Operation(
            summary = "Обновление пароля пользователя",
            description = "Получается данные пользователя"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Пароль изменен"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Токен устарел либо невалиден"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateCurrentUserPassword (
            @Parameter(name = "accessToken", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlIjoidXNlciIsImV4cCI6OTk5OTk5OTk5OX0.Qb4G6U7B8c9dE0fF1gH2iJ3kL4mN5oP6qR7sT8uV9wX0yZ")
            @RequestHeader("Authorization") String token,
            @Parameter(name = "Запрос на изменение пароля",schema = @Schema(implementation = PasswordChangeRequest.class))
            @RequestBody @Valid PasswordChangeRequest request
            ){
        userService.updateCurrentUserPassword(token, request);
        return ResponseEntity.noContent().build();
    }



    @Operation(
            summary = "Удаление текущего пользователя",
            description = "Удаление аккаунта текущего пользователя"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Данные пользователя"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Токен устарел либо невалиден"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCurrentUser(
            @Parameter(name = "accessToken", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlIjoidXNlciIsImV4cCI6OTk5OTk5OTk5OX0.Qb4G6U7B8c9dE0fF1gH2iJ3kL4mN5oP6qR7sT8uV9wX0yZ")
            @RequestHeader("Authorization") String token
    ){
        userService.deleteCurrentUser(token);
        return ResponseEntity.noContent().build();
    }
}
