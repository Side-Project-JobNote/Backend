package com.jobnote.global.exception;

import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JobNoteException.class)
    public ResponseEntity<ApiResponse<Void>> handleJobNotException(final JobNoteException e) {
        log.error("JobNoteException: ", e);
        ResponseCode responseCode = e.getResponseCode();
        return new ResponseEntity<>(ApiResponse.ofFail(responseCode), responseCode.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: ", e);
        ResponseCode responseCode = ResponseCode.INVALID_METHOD_ARGUMENT;
        return new ResponseEntity<>(ApiResponse.ofFail(responseCode, e.getBindingResult().getFieldErrors()), responseCode.getStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException: ", e);
        ResponseCode responseCode = ResponseCode.METHOD_NOT_ALLOWED;
        return new ResponseEntity<>(ApiResponse.ofFail(responseCode), responseCode.getStatus());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabledException(final DisabledException e) {
        log.error("DisabledException: ", e);
        ResponseCode responseCode = ResponseCode.INACTIVE_USER;
        return new ResponseEntity<>(ApiResponse.ofFail(responseCode), responseCode.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(final Exception e) {
        log.error("Exception: ", e);
        ResponseCode responseCode = ResponseCode.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(ApiResponse.ofFail(responseCode), responseCode.getStatus());
    }
}
