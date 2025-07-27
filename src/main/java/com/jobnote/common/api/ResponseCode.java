package com.jobnote.common.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // Success
    OK(HttpStatus.OK, "2000", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "2010", "리소스가 성공적으로 생성되었습니다."),

    // 400 Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "4000", "잘못된 요청입니다."),
    INVALID_METHOD_ARGUMENT(HttpStatus.BAD_REQUEST, "4001", "입력값 유효성 검증에 실패했습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "4010", "요청 리소스에 대한 액세스 권한이 없습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "4011", "액세스 토큰이 만료되었습니다."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "4012", "유효하지 않은 토큰 서명입니다."),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "4013", "잘못된 토큰 형식입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "4014", "지원하지 않는 토큰 형식입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "4015", "유효하지 않은 액세스 토큰입니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "4030", "요청 리소스에 대한 액세스가 금지되었습니다."),

    // 404 Not Found
    NOT_FOUND(HttpStatus.NOT_FOUND, "4040", "요청 리소스를 찾을 수 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "4041", "해당 사용자를 찾을 수 없습니다."),
    NOT_FOUND_APPLICATION_FORM(HttpStatus.NOT_FOUND, "4042", "해당 지원서를 찾을 수 없습니다."),
    NOT_FOUND_SCHEDULE(HttpStatus.NOT_FOUND, "4043", "해당 일정을 찾을 수 없습니다."),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "4050", "요청 메소드를 지원하지 않습니다."),

    // 409 Conflict
    CONFLICT(HttpStatus.CONFLICT, "4090", "요청이 서버의 상태와 충돌했습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5000", "서버에 에러가 발생했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
