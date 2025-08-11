package com.jobnote.global.common;

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
    INVALID_SCHEDULE_FORM_ASSOCIATION(HttpStatus.BAD_REQUEST, "4002", "일정이 지정한 지원서에 속하지 않습니다."),
    NOT_SUPPORTED_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, "4003", "해당 소셜 로그인은 지원되지 않습니다."),
    INVALID_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "4004", "올바르지 않은 토큰 타입입니다."),
    VERIFICATION_EMAIL_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "4005", "이미 인증이 완료된 인증 이메일입니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "4010", "요청 리소스에 대한 액세스 권한이 없습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "4011", "토큰이 만료되었습니다."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "4012", "유효하지 않은 토큰 서명입니다."),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "4013", "잘못된 토큰 형식입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "4014", "지원하지 않는 토큰 형식입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "4015", "유효하지 않은 토큰입니다."),
    INVALID_USERNAME_PASSWORD(HttpStatus.UNAUTHORIZED, "4016", "아이디 또는 비밀번호가 잘못되었습니다."),
    EXPIRED_VERIFICATION_EMAIL(HttpStatus.UNAUTHORIZED, "4017", "만료된 인증 이메일입니다."),
    UNAUTHORIZED_SOCIAL_LOGIN(HttpStatus.UNAUTHORIZED, "4018", "소셜 로그인 인증에 실패했습니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "4030", "요청 리소스에 대한 액세스가 금지되었습니다."),
    PENDING_EMAIL_VERIFICATION(HttpStatus.FORBIDDEN, "4031", "이메일 인증이 완료되지 않았습니다."),
    NOT_YET_SIGNED_UP(HttpStatus.FORBIDDEN, "4032", "회원가입 절차가 완료되지 않았습니다."),
    VERIFICATION_EMAIL_NOT_YET_VERIFIED(HttpStatus.FORBIDDEN, "4033", "인증이 완료되지 않은 인증 이메일입니다."),

    // 404 Not Found
    NOT_FOUND(HttpStatus.NOT_FOUND, "4040", "요청 리소스를 찾을 수 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "4041", "해당 사용자를 찾을 수 없습니다."),
    NOT_FOUND_APPLICATION_FORM(HttpStatus.NOT_FOUND, "4042", "해당 지원서를 찾을 수 없습니다."),
    NOT_FOUND_SCHEDULE(HttpStatus.NOT_FOUND, "4043", "해당 일정을 찾을 수 없습니다."),
    NOT_FOUND_VERIFICATION_EMAIL(HttpStatus.NOT_FOUND, "4044", "해당 인증 이메일을 찾을 수 없습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "4045", "해당 리프레시 토큰을 찾을 수 없습니다."),
    NOT_FOUND_DOCUMENT(HttpStatus.NOT_FOUND, "4046", "해당 문서를 찾을 수 없습니다."),
    NOT_FOUND_S3_FILE(HttpStatus.NOT_FOUND, "4047", "업로드된 문서가 아닙니다."),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "4050", "요청 메소드를 지원하지 않습니다."),

    // 409 Conflict
    CONFLICT(HttpStatus.CONFLICT, "4090", "요청이 서버의 상태와 충돌했습니다."),
    DUPLICATED_USER_NICKNAME(HttpStatus.CONFLICT, "4091", "이미 사용중인 닉네임입니다."),
    DUPLICATED_USER_EMAIL(HttpStatus.CONFLICT, "4092", "이미 가입된 이메일입니다."),
    USER_ALREADY_WITHDRAWN(HttpStatus.CONFLICT, "4093", "이미 탈퇴된 회원입니다."),

    // 413 Payload Too Large
    UPLOAD_SIZE_LIMIT_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "4130", "총 업로드 허용 용량 100MB을 초과했습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5000", "서버에 에러가 발생했습니다."),
    UNABLE_TO_SEND_MAIL(HttpStatus.INTERNAL_SERVER_ERROR, "5001", "메일을 전송하지 못했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
