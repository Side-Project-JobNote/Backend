package com.jobnote.auth.security;

import com.jobnote.auth.security.dto.CustomUserDetails;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static com.jobnote.global.common.Constants.*;
import static com.jobnote.global.common.ResponseCode.INVALID_ACCESS_TOKEN;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            final String accessToken = parseBearerToken(request)
                    .orElseThrow(() -> new JobNoteException(INVALID_ACCESS_TOKEN));

            final String email = tokenProvider.getEmailFromPayload(accessToken)
                    .orElseThrow(() -> new JobNoteException(INVALID_ACCESS_TOKEN));

            setAuthentication((CustomUserDetails) customUserDetailsService.loadUserByUsername(email));

        } catch (JobNoteException e) {
            request.setAttribute(ATTRIBUTE_EXCEPTION, e);
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> parseBearerToken(final HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(value -> StringUtils.hasText(value) && value.startsWith(AUTHORIZATION_TYPE_BEARER))
                .map(value -> value.substring(AUTHORIZATION_TYPE_BEARER.length()));
    }

    private void setAuthentication(final CustomUserDetails customUserDetails) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return new HashSet<>(List.of(WHITELIST)).contains(request.getRequestURI());
    }
}
