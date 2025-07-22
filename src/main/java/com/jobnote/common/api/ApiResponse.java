package com.jobnote.common.api;

import lombok.AccessLevel;
import lombok.Builder;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Builder(access = AccessLevel.PRIVATE)
public record ApiResponse<T>(
        String code,
        String message,
        List<FieldErrorDetail> details,
        T data
) {

    public static <T> ApiResponse<T> ofSuccess(ResponseCode responseCode) {
        return ApiResponse.<T>builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .build();
    }

    public static <T> ApiResponse<T> ofSuccess(ResponseCode responseCode, T data) {
        return ApiResponse.<T>builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ofFail(ResponseCode responseCode) {
        return ApiResponse.<T>builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .build();
    }

    public static <T> ApiResponse<T> ofFail(ResponseCode responseCode, List<FieldError> fieldErrors) {
        return ApiResponse.<T>builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .details(FieldErrorDetail.of(fieldErrors))
                .build();
    }

    public record FieldErrorDetail(
            String field,
            String message
    ) {

        private static FieldErrorDetail of(FieldError fieldError) {
            return new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage());
        }

        private static List<FieldErrorDetail> of(List<FieldError> fieldErrors) {
            return fieldErrors.stream()
                    .map(FieldErrorDetail::of)
                    .collect(Collectors.toList());
        }
    }
}
