package com.alicasts.generic_crud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
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

    @Bean
    public OpenApiCustomizer globalErrorResponses() {
        return openApi -> {
            if (openApi.getPaths() == null) return;
            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation -> {
                        ApiResponses responses = operation.getResponses();
                        addIfMissing(responses, "400", "Validation error");
                        addIfMissing(responses, "404", "Resource not found");
                        addIfMissing(responses, "409", "Resource conflict");
                        addIfMissing(responses, "500", "Internal error");
                    })
            );
        };
    }

    private void addIfMissing(ApiResponses responses, String code, String description) {
        if (!responses.containsKey(code)) {
            responses.addApiResponse(code, new ApiResponse().description(description));
        }
    }
}
