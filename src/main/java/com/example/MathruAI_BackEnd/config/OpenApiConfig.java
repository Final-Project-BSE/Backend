package com.example.MathruAI_BackEnd.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My API")
                        .version("1.0.0")
                        .description("API documentation for my Spring Boot application"))
                .externalDocs(new ExternalDocumentation()
                        .description("Spring Boot Documentation")
                        .url("https://spring.io/projects/spring-boot"));
    }
}
