package io.github.fcardozera.gamehub.identity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import io.github.fcardozera.gamehub.identity.dto.PlayerResponse;
import io.github.fcardozera.gamehub.identity.dto.RegisterRequest;
import io.github.fcardozera.gamehub.player.Player;
import io.github.fcardozera.gamehub.player.PlayerRepository;
import io.github.fcardozera.gamehub.identity.exception.EmailAlreadyUsedException;
import io.github.fcardozera.gamehub.identity.exception.NicknameAlreadyUsedException;

@SpringBootTest
@Testcontainers
class RegistrationServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    RegistrationService registrationService;

    @Autowired
    PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        playerRepository.deleteAll();
    }

    @Test
    void shouldRegisterPlayerSuccessfully() {
        // Arrange
        String username = "TestUser";
        String password = "testpassword";
        String email = "TestUser@example.com";

        RegisterRequest request = new RegisterRequest(email, password, username);

        // Act
        PlayerResponse playerResponse = registrationService.register(request);

        // Assert
        assertThat(playerResponse.email()).isEqualTo(email.toLowerCase());
        assertThat(playerResponse.createdAt()).isNotNull();
        assertThat(playerResponse.nickname()).isEqualTo(username);
        assertThat(playerRepository.existsByEmail(email.toLowerCase())).isTrue();
        assertThat(playerRepository.existsByNickname(username)).isTrue();
    }

    @Test
    void shouldNotRegisterPlayerWithExistingEmail() {
        // Arrange
        String username = "TestUser2";
        String password = "testpassword";
        String email = "TestUser@example.com";

        // REGISTER ORIGINAL PLAYER WITH THE SAME EMAIL
        registrationService.register(new RegisterRequest(email, password, "TestUser"));

        RegisterRequest request = new RegisterRequest(email, password, username);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.register(request))
                .isInstanceOf(EmailAlreadyUsedException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void shouldNotRegisterPlayerWithExistingNickname() {
        // Arrange
        String username = "TestUser";
        String password = "testpassword";
        String email = "TestUser2@example.com";

        // REGISTER ORIGINAL PLAYER WITH THE SAME NICKNAME
        registrationService.register(new RegisterRequest("TestUser@example.com", password, username));

        RegisterRequest request = new RegisterRequest(email, password, username);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.register(request))
                .isInstanceOf(NicknameAlreadyUsedException.class)
                .hasMessageContaining("Nickname already registered");
    }

    @Test
    void shouldNotRegisterPlayerWithExistingEmailButDifferentCase() {
        // Arrange
        String username = "TestUser3";
        String password = "testpassword";
        String email = "TESTUSER@example.com"; // UPPERCASE email

        // REGISTER ORIGINAL PLAYER WITH THE SAME EMAIL IN LOWERCASE
        registrationService.register(new RegisterRequest("testuser@example.com", password, "TestUser"));

        RegisterRequest request = new RegisterRequest(email, password, username);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.register(request))
                .isInstanceOf(EmailAlreadyUsedException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void shouldPasswordBeStoredAsHash() {
        // Arrange
        String username = "TestUser4";
        String password = "testpassword";
        String email = "TestUser4@example.com";
        RegisterRequest request = new RegisterRequest(email, password, username);

        // Act
        registrationService.register(request);
        Player registeredPlayer = playerRepository.findByEmail(email.toLowerCase());

        // Assert
        assertThat(registeredPlayer.getPasswordHash()).isNotEqualTo(password);
        assertThat(registeredPlayer.getPasswordHash()).startsWith("{bcrypt}");
    }

}