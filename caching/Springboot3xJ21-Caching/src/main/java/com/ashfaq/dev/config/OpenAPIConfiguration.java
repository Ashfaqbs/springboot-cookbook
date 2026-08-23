package com.ashfaq.dev.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
               .info(new Info()
                       .title("Product Management API")
                       .version("1.0.0")
                       .description("API for managing products in the inventory.")
                       .contact(new Contact()
                               .name("Ashfaq BS ")
                               .email("ashu@example.com")));
    }
}
