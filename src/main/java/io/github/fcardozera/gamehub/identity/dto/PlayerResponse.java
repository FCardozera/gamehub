package io.github.fcardozera.gamehub.identity.dto;

import io.github.fcardozera.gamehub.player.Player;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Data representing a player")
public record PlayerResponse(

        @Schema(description = "Player unique identifier", example = "1") Long id,
        @Schema(description = "Player email address", example = "player@example.com") String email,
        @Schema(description = "Unique public display name", example = "user123") String nickname,
        @Schema(description = "Account creation timestamp", example = "2023-01-01T12:00:00Z") Instant createdAt

) {
    public static PlayerResponse from(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getEmail(),
                player.getNickname(),
                player.getCreatedAt());
    }
}
