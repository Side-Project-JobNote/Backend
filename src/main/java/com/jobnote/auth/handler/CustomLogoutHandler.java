package com.jobnote.auth.handler;

import com.jobnote.domain.user.service.AuthTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import static com.jobnote.global.common.Constants.*;
import static com.jobnote.global.util.CookieUtil.getTokenFromCookie;
import static com.jobnote.global.util.CookieUtil.invalidateCookie;

@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final AuthTokenService authTokenService;

    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        authTokenService.invalidate(getTokenFromCookie(request, COOKIE_NAME_REFRESH_TOKEN));

        response.addHeader(HttpHeaders.SET_COOKIE, invalidateCookie(COOKIE_NAME_ACCESS_TOKEN));
        response.addHeader(HttpHeaders.SET_COOKIE, invalidateCookie(COOKIE_NAME_REFRESH_TOKEN));
        SecurityContextHolder.clearContext();
    }
}
