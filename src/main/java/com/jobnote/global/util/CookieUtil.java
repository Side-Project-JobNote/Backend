package com.jobnote.global.util;

import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Optional;

import static com.jobnote.global.common.ResponseCode.INVALID_TOKEN;

public class CookieUtil {

    public static String getTokenFromCookie(final HttpServletRequest request, final String name) {
        return getCookie(request, name)
                .orElseThrow(() -> new JobNoteException(INVALID_TOKEN))
                .getValue();
    }

    public static String createResponseCookie(final String name, final String value, final String path, final int maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .path(path)
                .maxAge(maxAge)
                // .secure(true) 운영 환경에서 설정
                .sameSite(String.valueOf(org.springframework.boot.web.server.Cookie.SameSite.STRICT))
                .build().toString();
    }

    public static String invalidateCookie(final String name) {
        return createResponseCookie(name, null, "/", 0);
    }

    private static Optional<Cookie> getCookie(final HttpServletRequest request, final String name) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst();
    }
}
