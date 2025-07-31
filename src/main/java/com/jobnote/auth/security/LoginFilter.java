package com.jobnote.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.auth.security.dto.CustomUserDetails;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.dto.UserLoginRequest;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static com.jobnote.global.common.Constants.ATTRIBUTE_EXCEPTION;
import static com.jobnote.global.common.Constants.CHARACTER_ENCODING;
import static com.jobnote.global.common.ResponseCode.BAD_REQUEST;

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
        tokenProvider.responseToken(response, objectMapper, principal.getUserId(), principal.getUsername());
    }

    @Override
    protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException failed) throws IOException {
        log.error("로그인 실패", failed);

        final ResponseCode responseCode = ResponseCode.INVALID_USERNAME_PASSWORD;
        response.setStatus(responseCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.ofFail(responseCode)));
    }
}
