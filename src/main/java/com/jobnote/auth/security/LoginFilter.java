package com.jobnote.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.auth.security.dto.CustomUserDetails;
import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.dto.UserLoginRequest;
import com.jobnote.domain.user.dto.UserTokenResponse;
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

import static com.jobnote.global.common.Constants.CHARACTER_ENCODING;

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
            throw new JobNoteException(ResponseCode.BAD_REQUEST);
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final Authentication authResult) throws IOException {
        final CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        final Long userId = customUserDetails.getId();
        final String role = customUserDetails.getAuthorities().iterator().next().getAuthority();

        final Token token = tokenProvider.issueToken(userId, role);

        log.info("로그인 성공 id: {}, role: {}", userId, role);

        final ResponseCode responseCode = ResponseCode.OK;
        response.setStatus(responseCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.ofSuccess(responseCode, UserTokenResponse.of(userId, token))));
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
