package com.securebank.ledger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI ledgerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Secure Ledger API")
                        .version("1.0.0")
                        .description("""
                                Transaction ledger for high-value clients.

                                **Authentication**
                                Call `POST /api/v1/auth/login` to obtain a JWT, then click
                                Authorize above and paste the token.

                                **Authorization model**
                                Every read and write is scoped to the authenticated user at the
                                query level. No endpoint accepts a user identifier as input.
                                """))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT issued by /api/v1/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}