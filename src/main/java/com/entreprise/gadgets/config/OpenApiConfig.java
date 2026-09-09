package com.entreprise.gadgets.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gadgetsOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("API Gestion des Gadgets Promotionnels")
            .description("Direction Commerciale et Marketing - Gestion du catalogue, "
                + "des approvisionnements, des demandes, des distributions et des inventaires.")
            .version("v0.1"));
    }
}
