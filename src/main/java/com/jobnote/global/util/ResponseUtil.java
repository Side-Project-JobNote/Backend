package com.jobnote.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;

import static com.jobnote.global.common.Constants.CHARACTER_ENCODING;

public class ResponseUtil {

    public static void responseError(final HttpServletResponse response, final ObjectMapper objectMapper, final ResponseCode responseCode) throws IOException {
        response.setStatus(responseCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.ofFail(responseCode)));
    }
}
