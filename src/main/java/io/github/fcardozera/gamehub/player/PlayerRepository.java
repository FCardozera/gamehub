package io.github.fcardozera.gamehub.player;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
