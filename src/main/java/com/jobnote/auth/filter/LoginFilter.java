package com.jobnote.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.global.util.ResponseUtil;
import com.jobnote.domain.user.dto.UserLoginRequest;
import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static com.jobnote.global.common.Constants.ATTRIBUTE_EXCEPTION;
import static com.jobnote.global.common.ResponseCode.BAD_REQUEST;
import static com.jobnote.global.common.ResponseCode.INVALID_USERNAME_PASSWORD;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        this.setUsernameParameter("email");
        try {
            UserLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password(), null);
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            request.setAttribute(ATTRIBUTE_EXCEPTION, BAD_REQUEST);
            throw new JobNoteException(BAD_REQUEST);
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final Authentication authResult) throws IOException {
        final CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();
        tokenProvider.responseToken(response, objectMapper, principal.getUsername());
    }

    @Override
    protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException failed) throws IOException {
        log.error("로그인 실패", failed);
        ResponseUtil.responseError(response, objectMapper, INVALID_USERNAME_PASSWORD);
    }
}
