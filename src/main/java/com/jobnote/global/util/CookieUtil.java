package com.jobnote.global.util;

import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Arrays;

import static com.jobnote.global.common.ResponseCode.INVALID_COOKIE;

public class CookieUtil {

    public static String getValueFromCookie(final Cookie[] cookies, final String name) {
        if (cookies == null) {
            throw new JobNoteException(INVALID_COOKIE);
        }

        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst()
                .orElseThrow(() -> new JobNoteException(INVALID_COOKIE))
                .getValue();
    }

    public static ResponseCookie createResponseCookie(final String name, final String value, final String path, final Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .path(path)
                .maxAge(maxAge)
                .secure(true)
                .sameSite(String.valueOf(org.springframework.boot.web.server.Cookie.SameSite.NONE))
                .build();
    }

    public static ResponseCookie invalidateCookie(final String name, final String path) {
        return createResponseCookie(name, "", path, Duration.ZERO);
    }
}
