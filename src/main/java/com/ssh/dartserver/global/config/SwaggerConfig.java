package com.ssh.dartserver.global.config;

import com.ssh.dartserver.global.config.properties.SwaggerProperty;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    private static final String SECURITY_SCHEME_NAME = "AccessToken";

    private final SwaggerProperty swaggerProperty;

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .title(swaggerProperty.getInfo().getTitle())
                .version(swaggerProperty.getInfo().getVersion())
                .description(swaggerProperty.getInfo().getDescription());

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(SECURITY_SCHEME_NAME);

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(
                        new Components()
                                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .name(SECURITY_SCHEME_NAME)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .info(info);
    }
}
