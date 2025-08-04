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

import static com.jobnote.global.common.ResponseCode.UNAUTHORIZED_SOCIAL_LOGIN;

@RequiredArgsConstructor
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ResponseUtil.responseError(response, objectMapper, UNAUTHORIZED_SOCIAL_LOGIN);
    }
}
