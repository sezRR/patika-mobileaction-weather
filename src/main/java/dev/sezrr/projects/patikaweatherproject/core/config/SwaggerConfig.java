package dev.sezrr.projects.patikaweatherproject.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig
{
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Patika & MobileAction | Weather Project - RESTful API")
                                .description("This is a RESTful API for the Patika & MobileAction Weather Project. It provides endpoints to manage cities and their pollution data.")
                                .version("1.0")
                );
    }

    @Bean
    public GroupedOpenApi apiV1() {
        return GroupedOpenApi.builder()
                .group("api-v1")
                .pathsToMatch("/v1/**")
                .build();
    }
}
