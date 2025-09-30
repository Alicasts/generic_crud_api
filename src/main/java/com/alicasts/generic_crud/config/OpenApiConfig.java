package com.alicasts.generic_crud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI genericCrudOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Generic CRUD Spring Project - API")
                        .description("""
                        REST API for a generic CRUD demonstrating Clean Code, MapStruct, validation and a standardized error model.
                        """)
                        .version("v1.0.0")
                        .contact( new Contact()
                                .name("Ali Castilho")
                                .email("ali.cast.santos@gmail.com"))
                        .license(new License().name("MIT")))
                .addServersItem(new Server().url("/"));
    }
}
