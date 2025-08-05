package com.jobnote.global.config.swagger;

import java.util.Map;

import com.jobnote.global.annotation.swagger.ApiResponseExplanations;
import com.jobnote.global.annotation.swagger.ApiSuccessResponseExplanation;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Component
public class ApiSuccessResponseHandler {

	private static final String APPLICATION_JSON = "application/json";
	private static final int SUCCESS_CODE = 2000;

	public void handleApiSuccessResponse(Operation operation, HandlerMethod handlerMethod) {
		ApiResponseExplanations apiResponseExplanations = handlerMethod.getMethodAnnotation(ApiResponseExplanations.class);

		if (apiResponseExplanations == null) {
			return;
		}

		ApiSuccessResponseExplanation apiSuccessResponseExplanation = apiResponseExplanations.success();

		if (apiSuccessResponseExplanation != null) {
			ApiResponses responses = operation.getResponses();
			// 기본 200 OK 응답이 존재하면 제거
			responses.remove("200");

			Schema<?> responseSchema = new Schema<>()
				.addProperty("code",
					new Schema<>().type("integer").example(SUCCESS_CODE))
				.addProperty("message", new Schema<>().example(null))
				.addProperty("data",
					apiSuccessResponseExplanation.responseClass()
						.isAssignableFrom(ApiSuccessResponseExplanation.EmptyClass.class)
						?
						new Schema<>().type("object").example(Map.of())
						:
						new Schema<>().$ref(
							"#/components/schemas/" + apiSuccessResponseExplanation.responseClass().getSimpleName())
				);

			ApiResponse apiResponse = new ApiResponse()
				.description(apiSuccessResponseExplanation.description())
				.content(
					new Content()
						.addMediaType(
							APPLICATION_JSON,
							new MediaType().schema(responseSchema)
						)
				);
			responses.addApiResponse(String.valueOf(apiSuccessResponseExplanation.status().value()), apiResponse);
		}
	}
}
