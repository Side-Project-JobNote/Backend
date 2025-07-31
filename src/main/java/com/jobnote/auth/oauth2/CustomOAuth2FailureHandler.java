package com.jobnote.auth.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.global.util.ResponseUtil;
import com.jobnote.global.common.ResponseCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

@RequiredArgsConstructor
@Configuration
public class CustomOAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ResponseUtil.responseError(response, objectMapper, ResponseCode.UNAUTHORIZED_SOCIAL_LOGIN);
    }
}
