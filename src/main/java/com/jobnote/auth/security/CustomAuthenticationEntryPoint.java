package com.jobnote.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.global.util.ResponseUtil;
import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static com.jobnote.global.common.Constants.ATTRIBUTE_EXCEPTION;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException {
        final JobNoteException e = (JobNoteException) request.getAttribute(ATTRIBUTE_EXCEPTION);
        final ResponseCode responseCode = e == null ? ResponseCode.FORBIDDEN : e.getResponseCode();

        log.error("AuthenticationException: ", e == null ? authException : e);

        ResponseUtil.responseError(response, objectMapper, responseCode);
    }
}
