package com.starwars.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
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