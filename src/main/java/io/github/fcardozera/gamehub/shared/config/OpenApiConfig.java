package io.github.fcardozera.gamehub.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gameHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GameHub API")
                        .version("v1")
                        .description("""
                                Backend API for a multiplayer game: accounts, matchmaking, \
                                ranking and in-game economy.""")
                        .contact(new Contact()
                                .name("Felipe Bender Cardoso")
                                .url("https://github.com/fcardozera"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}