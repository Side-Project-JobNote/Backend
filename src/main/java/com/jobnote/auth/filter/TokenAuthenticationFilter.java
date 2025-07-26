package com.jobnote.auth.filter;

import com.jobnote.auth.token.TokenProvider;
import com.jobnote.common.api.ResponseCode;
import com.jobnote.common.exception.JobNoteException;
import com.jobnote.common.properties.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static com.jobnote.common.Constants.*;

@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String accessToken = parseBearerToken(request, HttpHeaders.AUTHORIZATION)
                    .orElseThrow(() -> new JobNoteException(ResponseCode.INVALID_ACCESS_TOKEN));

            final long userId = tokenProvider.getUserIdFromPayload(accessToken)
                    .orElseThrow(() -> new JobNoteException(ResponseCode.INVALID_ACCESS_TOKEN));

            final String role = tokenProvider.getRoleFromPayload(accessToken)
                    .orElseThrow(() -> new JobNoteException(ResponseCode.INVALID_ACCESS_TOKEN));

            SecurityContextHolder.getContext().setAuthentication(getAuthentication(userId, role));

        } catch (JobNoteException e) {
            request.setAttribute(ATTRIBUTE_EXCEPTION, e);
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> parseBearerToken(final HttpServletRequest request, final String headerName) {
        return Optional.ofNullable(request.getHeader(headerName))
                .filter(value -> StringUtils.hasText(value) && value.startsWith(AUTHORIZATION_TYPE_BEARER))
                .map(value -> value.substring(AUTHORIZATION_TYPE_BEARER.length()));
    }

    private Authentication getAuthentication(final Long userId, final String role) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.singleton(new SimpleGrantedAuthority(role)));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new HashSet<>(securityProperties.getWhitelist()).contains(request.getRequestURI());
    }
}
