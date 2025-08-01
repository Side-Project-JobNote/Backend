package com.jobnote.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;

import java.io.IOException;

import static com.jobnote.global.common.Constants.CHARACTER_ENCODING;

public class ResponseUtil {

    public static void responseError(final HttpServletResponse response, final ObjectMapper objectMapper, final ResponseCode responseCode) throws IOException {
        response.setStatus(responseCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.ofFail(responseCode)));
    }

    public static String createResponseCookie(final String name, final String value, final String path, final int maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .path(path)
                .maxAge(maxAge)
                // .secure(true) 운영 환경에서 설정
                .sameSite(String.valueOf(Cookie.SameSite.STRICT))
                .build().toString();
    }
}
