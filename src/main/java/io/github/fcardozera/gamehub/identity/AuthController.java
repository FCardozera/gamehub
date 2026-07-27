package io.github.fcardozera.gamehub.identity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import io.github.fcardozera.gamehub.identity.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.github.fcardozera.gamehub.identity.dto.PlayerResponse;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Account registration and login")
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new player", description = """
            Creates a player account. The email is normalised to lower case \
            and the password is stored as a hash.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Player successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Email or nickname already in use", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public PlayerResponse register(@Valid @RequestBody RegisterRequest request) {
        return registrationService.register(request);
    }

}
