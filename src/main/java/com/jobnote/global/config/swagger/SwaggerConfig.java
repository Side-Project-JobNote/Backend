package com.jobnote.global.config.swagger;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import lombok.RequiredArgsConstructor;

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
}
