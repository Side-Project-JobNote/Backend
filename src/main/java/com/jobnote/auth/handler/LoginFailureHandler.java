package com.jobnote.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.global.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.jobnote.global.common.Constants.URI_USER_LOGIN;
import static com.jobnote.global.common.Constants.URI_USER_OAUTH2;
import static com.jobnote.global.common.ResponseCode.INVALID_USERNAME_PASSWORD;
import static com.jobnote.global.common.ResponseCode.UNAUTHORIZED_SOCIAL_LOGIN;

@RequiredArgsConstructor
@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (URI_USER_LOGIN.equals(request.getRequestURI())) {
            ResponseUtil.responseError(response, objectMapper, INVALID_USERNAME_PASSWORD);
        }
        if (request.getRequestURI().startsWith(URI_USER_OAUTH2)) {
            ResponseUtil.responseError(response, objectMapper, UNAUTHORIZED_SOCIAL_LOGIN);
        }
    }
}
