package com.mca.project.online_voting.config;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI bean with custom information.
     * This information will be displayed at the top of the Swagger UI.
     *
     * @return An OpenAPI object with custom metadata.
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Your Application API") // Main title of your API
                        .description("API documentation for your Spring Boot application managing Users, Companies, and User Roles.") // A brief description
                        .version("v1.0.0") // API version
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))) // License information
                .externalDocs(new ExternalDocumentation() // Optional: Link to external documentation
                        .description("Your Application Wiki Documentation")
                        .url("https://your-wiki-url.com/docs"));
    }

    // You can add more @Bean methods here if you need to customize specific groups
    // or paths for different parts of your API.
    // For example:
    /*
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**") // Include all paths under /api/
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/admin/**") // Include all paths under /admin/
                .build();
    }
    */
}