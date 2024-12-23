package com.hrm.books.utilities.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LINK: https://www.baeldung.com/openapi-jwt-authentication
 * <br/>
 * OpenAPI user guide
 */

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customizeOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
//    @Bean
//    public SecurityScheme securityScheme(){
//        return new SecurityScheme()
//                .name("Bearer Authentication")
//                .type(SecurityScheme.Type.HTTP)
//                .bearerFormat("JWT")
//                .scheme("bearer");
//    }
    /**
     * LINK: https://kungfutech.edu.vn/bai-viet/spring-boot/tao-api-document-voi-spring-boot-va-open-api
     */
    @Bean
    public GroupedOpenApi openApi() {
        return GroupedOpenApi.builder()
                .group("application-book")
                .packagesToScan("com.hrm.books.controller")
//                .pathsToExclude("/my-account/**")
//                .pathsToMatch("/my-account/**")
                .build();
    }
}
