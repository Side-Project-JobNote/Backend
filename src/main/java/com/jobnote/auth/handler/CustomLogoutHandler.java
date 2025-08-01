package com.jobnote.auth.handler;

import com.jobnote.domain.user.service.AuthTokenService;
import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import static com.jobnote.global.common.Constants.*;
import static com.jobnote.global.common.ResponseCode.INVALID_TOKEN;
import static com.jobnote.global.util.CookieUtil.getCookie;
import static com.jobnote.global.util.ResponseUtil.createResponseCookie;

@RequiredArgsConstructor
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final AuthTokenService authTokenService;

    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        final String refreshToken = getCookie(request, COOKIE_NAME_REFRESH_TOKEN)
                .orElseThrow(() -> new JobNoteException(INVALID_TOKEN))
                .getValue();

        authTokenService.invalidate(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE,
                createResponseCookie(COOKIE_NAME_ACCESS_TOKEN, null, "/", 0));

        response.addHeader(HttpHeaders.SET_COOKIE,
                createResponseCookie(COOKIE_NAME_REFRESH_TOKEN, null, "/", 0));

        SecurityContextHolder.clearContext();
    }
}
