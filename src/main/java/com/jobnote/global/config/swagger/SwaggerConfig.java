package com.jobnote.global.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final ApiSuccessResponseHandler apiSuccessResponseHandler;
    private final ApiErrorResponseHandler apiErrorResponseHandler;

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");


        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security(List.of(securityRequirement))
                .info(info());
    }

    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            apiSuccessResponseHandler.handleApiSuccessResponse(operation, handlerMethod);
            apiErrorResponseHandler.handleApiErrorResponse(operation, handlerMethod);
            return operation;
        };
    }

    @PostConstruct
    public void customizeLocalDateTime() {
        SpringDocUtils.getConfig().replaceWithSchema(LocalDateTime.class,
                new StringSchema()
                        .example("2025-08-06T00:00:00")
                        .format("date-time"));
    }

    private Info info() {
        return new Info()
                .title("백엔드 API 명세서")
                .description("전체 예외(응답) 코드 목록은 Notion에서 확인하실 수 있습니다.")
                .version("v1.0.0");
    }
}
