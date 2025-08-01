package com.jobnote.auth.filter;

import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.auth.service.CustomUserDetailsService;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static com.jobnote.global.common.Constants.*;
import static com.jobnote.global.common.ResponseCode.INVALID_TOKEN;
import static com.jobnote.global.util.CookieUtil.getCookie;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            if (URI_USER_REISSUE.equals(request.getRequestURI())) {
                validateRefreshToken(request);
            } else {
                authenticate(request);
            }
        } catch (JobNoteException e) {
            request.setAttribute(ATTRIBUTE_EXCEPTION, e);
        }

        filterChain.doFilter(request, response);
    }

    private void validateRefreshToken(final HttpServletRequest request) {
        final String refreshToken = getCookie(request, COOKIE_NAME_REFRESH_TOKEN)
                .orElseThrow(() -> new JobNoteException(INVALID_TOKEN))
                .getValue();

        tokenProvider.validateRefreshToken(refreshToken);
    }

    private void authenticate(final HttpServletRequest request) {
        final String accessToken = getCookie(request, COOKIE_NAME_ACCESS_TOKEN)
                .orElseThrow(() -> new JobNoteException(INVALID_TOKEN))
                .getValue();

        final String email = tokenProvider.getEmailFromPayload(accessToken)
                .orElseThrow(() -> new JobNoteException(INVALID_TOKEN));

        setAuthentication(email);
    }

    private void setAuthentication(final String email) {
        CustomPrincipal principal = (CustomPrincipal) customUserDetailsService.loadUserByUsername(email);
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return new HashSet<>(List.of(WHITELIST)).contains(request.getRequestURI());
    }
}
