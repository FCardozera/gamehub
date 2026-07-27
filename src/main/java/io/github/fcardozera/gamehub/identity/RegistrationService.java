package io.github.fcardozera.gamehub.identity;

import io.github.fcardozera.gamehub.identity.dto.PlayerResponse;
import io.github.fcardozera.gamehub.identity.dto.RegisterRequest;
import io.github.fcardozera.gamehub.identity.exception.EmailAlreadyUsedException;
import io.github.fcardozera.gamehub.identity.exception.NicknameAlreadyUsedException;
import io.github.fcardozera.gamehub.player.Player;
import io.github.fcardozera.gamehub.player.PlayerRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class RegistrationService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PlayerResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(); // Design decision: emails are case-insensitive, so we
                                                             // normalize them to lowercase
        String nickname = request.nickname().trim(); // Design decision: nicknames are case-sensitive, so we keep the
                                                     // original case

        if (playerRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }

        if (playerRepository.existsByNickname(nickname)) {
            throw new NicknameAlreadyUsedException(nickname);
        }

        Player player = new Player(
                email,
                passwordEncoder.encode(request.password()),
                nickname);

        return PlayerResponse.from(playerRepository.save(player));
    }
}
