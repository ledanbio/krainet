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
    public ResponseEntity<UserDataResponse> me(){
        UserDataResponse response = userService.getMe();
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
            @Parameter(name = "Измененные данные пользователя", schema = @Schema(implementation = UserUpdateRequest.class))
            @RequestBody @Valid UserUpdateRequest request
    ){
        UserDataResponse response = userService.updateUserData(request);

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
            @Parameter(name = "Запрос на изменение пароля",schema = @Schema(implementation = PasswordChangeRequest.class))
            @RequestBody @Valid PasswordChangeRequest request
            ){
        userService.updateCurrentUserPassword(request);
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
    public ResponseEntity<Void> deleteCurrentUser(){
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}
