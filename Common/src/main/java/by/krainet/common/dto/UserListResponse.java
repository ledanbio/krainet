package by.krainet.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Schema(description = "Список пользователей")
public record UserListResponse(
        @Schema(description = "Список пользователей")
        List<UserSummary> users,

        @Schema(description = "Общее количество", example = "150")
        long totalCount
) {
    @Builder
    @Schema(description = "Краткая информация о пользователе")
    public record UserSummary(
            @Schema(description = "ID пользователя", example = "42")
            Long id,

            @Schema(description = "Имя пользователя", example = "john_doe")
            String username,

            @Schema(description = "Email", example = "john@example.com")
            String email
    ) {}
}