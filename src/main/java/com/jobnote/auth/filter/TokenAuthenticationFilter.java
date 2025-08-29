package com.jobnote.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.auth.service.CustomUserDetailsService;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.global.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.jobnote.global.common.Constants.*;
import static com.jobnote.global.common.ResponseCode.INVALID_HEADER;
import static com.jobnote.global.common.ResponseCode.INVALID_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            authenticate(request);
        } catch (JobNoteException e) {
            log.warn("TokenAuthenticationFilter Exception: ", e);
            ResponseUtil.responseError(response, objectMapper, e.getResponseCode());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(final HttpServletRequest request) {
        final String accessToken = validateAuthorizationHeader(request);
        final String email = tokenProvider.getEmailFromPayload(accessToken)
                .orElseThrow(() -> new JobNoteException(INVALID_TOKEN));

        setAuthentication(email);
    }

    private void setAuthentication(final String email) {
        CustomUserDetails principal = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private String validateAuthorizationHeader(final HttpServletRequest request) {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(header) || !header.startsWith(BEARER)) {
            throw new JobNoteException(INVALID_HEADER);
        }

        return header.substring(BEARER.length());
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return TOKEN_FILTER_WHITELIST.contains(request.getRequestURI());
    }
}
