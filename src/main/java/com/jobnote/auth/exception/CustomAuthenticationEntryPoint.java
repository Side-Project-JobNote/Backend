package com.jobnote.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.domain.user.service.AuthTokenService;
import com.jobnote.global.util.ResponseUtil;
import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.jobnote.global.common.Constants.*;
import static com.jobnote.global.common.Constants.COOKIE_NAME_REFRESH_TOKEN;
import static com.jobnote.global.common.ResponseCode.EXPIRED_TOKEN;
import static com.jobnote.global.util.CookieUtil.getTokenFromCookie;
import static com.jobnote.global.util.CookieUtil.invalidateCookie;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final AuthTokenService authTokenService;

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws IOException {
        final JobNoteException e = (JobNoteException) request.getAttribute(ATTRIBUTE_EXCEPTION);
        final ResponseCode responseCode = e == null ? ResponseCode.FORBIDDEN : e.getResponseCode();

        log.error("AuthenticationException: ", e == null ? authException : e);

        if (URI_USER_REISSUE.equals(request.getRequestURI())) {
            response.addHeader(HttpHeaders.SET_COOKIE, invalidateCookie(COOKIE_NAME_ACCESS_TOKEN));
            response.addHeader(HttpHeaders.SET_COOKIE, invalidateCookie(COOKIE_NAME_REFRESH_TOKEN));
            SecurityContextHolder.clearContext();

            if (EXPIRED_TOKEN.equals(responseCode)) {
                authTokenService.invalidate(getTokenFromCookie(request, COOKIE_NAME_REFRESH_TOKEN));
            }
        }

        ResponseUtil.responseError(response, objectMapper, responseCode);
    }
}
