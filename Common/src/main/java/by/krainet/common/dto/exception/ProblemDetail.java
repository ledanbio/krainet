package by.krainet.common.dto.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.net.URI;
import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Problem Detail (RFC 7807)")
public record ProblemDetail(
   @Schema(description = "URI типа ошибки", example = "https://api.krainet.by/errors/user-not-found")
   URI type,

   @Schema(description = "Краткое название ошибки", example = "User not found")
   String title,

   @Schema(description = "HTTP статус код", example = "404")
   int status,

   @Schema(description = "Подробное описание", example = "User with id=67 not found")
   String detail,

   @Schema(description = "URI запроса", example = "/api/admin/users/67")
   URI instance,

   @Schema(description = "Время ошибки", example = "2026-06-14T20:55:00Z")
   Instant timestamp
) {}
