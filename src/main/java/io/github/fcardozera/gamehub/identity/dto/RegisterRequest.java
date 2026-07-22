package io.github.fcardozera.gamehub.identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data required to register a new player")
public record RegisterRequest(

        @Schema(description = "Player email address", example = "player@example.com") @NotBlank @Email @Size(max = 255) String email,

        @Schema(description = "Plain text password, hashed before storage", example = "s3cur3pass") @NotBlank @Size(min = 8, max = 100) String password,

        @Schema(description = "Unique public display name", example = "user123") @NotBlank @Size(min = 3, max = 32) String nickname

) {
}
