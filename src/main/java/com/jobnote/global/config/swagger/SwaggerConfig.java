package com.jobnote.global.config.swagger;

import io.swagger.v3.oas.models.media.StringSchema;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

	private final ApiSuccessResponseHandler apiSuccessResponseHandler;
	private final ApiErrorResponseHandler apiErrorResponseHandler;

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
}
