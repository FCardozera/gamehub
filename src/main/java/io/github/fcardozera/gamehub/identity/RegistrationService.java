package io.github.fcardozera.gamehub.identity;

import io.github.fcardozera.gamehub.identity.dto.PlayerResponse;
import io.github.fcardozera.gamehub.identity.dto.RegisterRequest;
import io.github.fcardozera.gamehub.identity.exception.EmailAlreadyUsedException;
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

        if (playerRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }

        // TODO: apply the same verification to the nickname, with new exception, no
        // need to normalize it, but trim it to avoid leading/trailing spaces

        Player player = new Player(
                email,
                passwordEncoder.encode(request.password()),
                request.nickname().trim());

        return PlayerResponse.from(playerRepository.save(player));
    }
}
