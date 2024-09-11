package ua.edu.ukma.cleaning.utils.swagger;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Value("${project.version}")
    private String version;
    @Value("${domain.back}")
    private String devServer;

    @Bean
    public OpenAPI customOpenAPI() {
        String securitySchemeName = "jwt";
        SecurityScheme securityScheme = new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("Cleaning application")
                        .version(version)
                        .description("Cleaning application."))
                .components(new Components().addSecuritySchemes(securitySchemeName, securityScheme))
                .servers(List.of(new Server().url(devServer)));
    }
}
