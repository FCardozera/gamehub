package io.github.fcardozera.gamehub.identity.dto;

import io.github.fcardozera.gamehub.player.Player;
import java.time.Instant;

public record PlayerResponse(

        Long id,
        String email,
        String nickname,
        Instant createdAt

) {
    public static PlayerResponse from(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getEmail(),
                player.getNickname(),
                player.getCreatedAt());
    }
}
