package by.krainet.auth.Controller;

import by.krainet.auth.Service.AdminService;
import by.krainet.common.dto.AdminEmailResponse;
import by.krainet.common.dto.UserDataResponse;
import by.krainet.common.dto.UserListResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Панель админа", description = "Управление пользователями: CRUD, назначение ролей, поиск")
public class AdminCont {

    private final AdminService adminService;

    @Operation(
            summary = "Получение данных пользователя",
            description = "Получение данных пользователя по айди. Требует роль ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение данных пользователя",
                    content = @Content(schema = @Schema(implementation = UserDataResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав (не ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDataResponse> getDataByUserId(
            @Parameter(description = "ID пользователя", example = "42", required = true)
            @PathVariable Long id
    ){
        return ResponseEntity.ok(adminService.getUserByUserId(id));
    }

    @Operation(
            summary = "Обновление данных пользователя",
            description = "Изменяет имя, фамилию, email, username пользователя по айди. Требует роль ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные пользователя успешно обновлены",
                    content = @Content(schema = @Schema(implementation = UserDataResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав (не ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDataResponse> updateDataByUserId(
            @Parameter(description = "ID пользователя", example = "42", required = true)
            @PathVariable Long id,

            @Valid
            @RequestBody
            @Parameter(description = "Новые данные пользователя", required = true)
            UserUpdateRequest request
    ){
        return ResponseEntity.ok(adminService.updateDataByUserId(id, request));
    }

    @Operation(
            summary = "Обновление пароля пользователя",
            description = "Устанавливает новый пароль для пользователя по айди. Требует роль ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пароль успешно изменён"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Пароль не проходит валидацию",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав (не ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/users/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUserPasswordByUserId(
            @Parameter(description = "ID пользователя", example = "42", required = true)
            @PathVariable Long id,

            @Valid
            @RequestBody
            @Parameter(description = "Новый пароль", required = true)
            String newPassword
    ){
        adminService.updateUserPasswordByUserId(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Безвозвратно удаляет пользователя и все связанные данные по айди. Требует роль ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав (не ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserByUserId(
            @Parameter(description = "ID пользователя", example = "42", required = true)
            @PathVariable Long id
    ){
        adminService.deleteUserByUserId(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Получение email администраторов",
            description = "Возвращает список email всех пользователей с ролью ADMIN. Доступно для ролей ADMIN и SERVICE."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список email получен",
                    content = @Content(schema = @Schema(implementation = AdminEmailResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав (не ADMIN и не SERVICE)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/emails")
    @PreAuthorize("hasAnyRole('ADMIN','SERVICE')")
    public ResponseEntity<AdminEmailResponse> getAdminEmails(){
        return ResponseEntity.ok(adminService.getAdminEmails());
    }


    @Operation(
            summary = "Назначить пользователя администратором",
            description = "Изменяет роль пользователя на ADMIN. Только для существующих пользователей с ролью USER. Требует роль ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Роль успешно изменена",
                    content = @Content(schema = @Schema(implementation = UserDataResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Пользователь уже является администратором",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав (не ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/users/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDataResponse> promoteUserToAdmin(
            @Parameter(description = "ID пользователя", example = "42", required = true)
            @PathVariable Long id
    ){
        return ResponseEntity.ok(adminService.promoteUserToAdmin(id));
    }




    @Operation(
            summary = "Получить список всех пользователей",
            description = "Возвращает всех пользователей с id, username и email. Требует роль ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список пользователей получен",
                    content = @Content(schema = @Schema(implementation = UserListResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав (не ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserListResponse> getAllUsers(){
        return ResponseEntity.ok(adminService.getAllUsers());
    }



    @Operation(
            summary = "Поиск пользователя по username",
            description = "Возвращает данные пользователя с id, username, email по точному совпадению. Требует роль ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден",
                    content = @Content(schema = @Schema(implementation = UserDataResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав (не ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDataResponse> getUserByUsername(
            @Parameter(description = "Username для поиска", example = "john_doe", required = true)
            @RequestParam String username
    ){
        return ResponseEntity.ok(adminService.getUserByUsername(username));
    }
}