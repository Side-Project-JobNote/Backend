package com.jobnote.global.util;

import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import static com.jobnote.global.common.ResponseCode.INVALID_TOKEN;

public class CookieUtil {

    public static String getTokenFromCookie(final Cookie[] cookies, final String name) {
        return getCookie(cookies, name)
                .orElseThrow(() -> new JobNoteException(INVALID_TOKEN))
                .getValue();
    }

    public static ResponseCookie createResponseCookie(final String name, final String value, final String path, final Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .path(path)
                .maxAge(maxAge)
                // .secure(true) 운영 환경에서 설정
                .sameSite(String.valueOf(org.springframework.boot.web.server.Cookie.SameSite.STRICT))
                .build();
    }

    public static ResponseCookie invalidateCookie(final String name) {
        return createResponseCookie(name, "", "/", Duration.ZERO);
    }

    private static Optional<Cookie> getCookie(final Cookie[] cookies, final String name) {
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst();
    }
}
