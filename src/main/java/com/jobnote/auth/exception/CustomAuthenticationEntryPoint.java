package com.jobnote.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.global.util.ResponseUtil;
import com.jobnote.global.common.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException {
        log.warn("AuthenticationEntryPoint: ", authException);
        ResponseUtil.responseError(response, objectMapper, ResponseCode.UNAUTHORIZED);
    }
}
