package com.kalsym.usersservice.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Sarosh
 */
@Configuration
//@OpenAPIDefinition
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        Components comps = new Components().addSecuritySchemes("Auth",
                new SecurityScheme()
                        .name("Auth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer"));
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("Auth"))
                .components(comps);

    }


    private Info apiInfo() {
        Contact contact = new Contact()
                .email("support@kalsym.com")
                .name("KALSYM")
                .url("https://kalsym.com");
        return new Info()
                .version("0.0.1-SNAPSHOT")
                .description("Users managment and auth.")
                .title("users-service")
                .contact(contact);
    }


}
