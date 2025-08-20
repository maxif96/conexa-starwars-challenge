package com.starwars.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(Arrays.asList(
                        new Server().url("https://conexa-starwars-api-f5c72652ce2f.herokuapp.com").description("Heroku Production"),
                        new Server().url("http://localhost:8080").description("Local Development")
                ))
                .tags(Arrays.asList(
                        new Tag().name("A. Authentication").description("Endpoints para autenticación y registro de usuarios"),
                        new Tag().name("B. Films").description("Endpoints para gestionar películas de Star Wars"),
                        new Tag().name("C. People").description("Endpoints para gestionar personajes de Star Wars"),
                        new Tag().name("D. Starships").description("Endpoints para gestionar naves espaciales de Star Wars"),
                        new Tag().name("E. Vehicles").description("Endpoints para gestionar vehículos de Star Wars")
                ))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa tu token JWT aquí. Obtén el token desde el endpoint de login o registro.")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(new Info()
                        .title("Star Wars API")
                        .description("API de Star Wars con autenticación JWT - Challenge Técnico Conexa")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Maxif96")
                                .email("maxifop96@gmail.com")
                                .url("https://github.com/maxif96"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}

