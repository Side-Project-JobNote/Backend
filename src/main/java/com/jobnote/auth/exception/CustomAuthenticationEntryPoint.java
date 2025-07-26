package com.jobnote.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.common.api.ApiResponse;
import com.jobnote.common.api.ResponseCode;
import com.jobnote.common.exception.JobNoteException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.jobnote.common.Constants.ATTRIBUTE_EXCEPTION;
import static com.jobnote.common.Constants.CHARACTER_ENCODING;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        final JobNoteException e = (JobNoteException) request.getAttribute(ATTRIBUTE_EXCEPTION);
        log.error("AuthenticationException: ", e);

        final ResponseCode responseCode = e.getResponseCode();
        response.setStatus(responseCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.ofFail(responseCode)));
    }
}
