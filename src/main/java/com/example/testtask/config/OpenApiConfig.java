package com.example.testtask.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI-документации.
 *
 * Бин OpenAPI задаёт:
 *   1) Метаданные API (title, version, description) - отображаются в шапке Swagger UI
 *   2) Security scheme "bearerAuth" - добавляет кнопку "Authorize" в Swagger UI,
 *      куда можно вставить JWT, и он будет автоматически подставляться в заголовок
 *      Authorization для всех защищённых эндпоинтов.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Test Task API")
                        .version("1.0.0")
                        .description("Сервис периодического опроса внешнего API с публикацией в Kafka. "
                                + "REST CRUD над сохранёнными данными, защита через JWT."))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
