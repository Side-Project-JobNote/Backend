package com.jobnote.global.util;

import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

import static com.jobnote.global.common.Constants.COOKIE_NAME_ACCESS_TOKEN;
import static com.jobnote.global.common.Constants.COOKIE_PATH_ACCESS_TOKEN;
import static com.jobnote.global.common.ResponseCode.INVALID_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CookieUtilTest {

    @Nested
    @DisplayName("토큰 쿠키 조회")
    class AccessTokenCookie {

        final String accessToken = "test.access.token";

        @Test
        @DisplayName("성공")
        void success() {
            // given
            Cookie[] cookies = new Cookie[]{new Cookie(COOKIE_NAME_ACCESS_TOKEN, accessToken)};

            // when
            String result = CookieUtil.getTokenFromCookie(cookies, COOKIE_NAME_ACCESS_TOKEN);

            // then
            assertThat(result).isEqualTo(accessToken);
        }

        @Test
        @DisplayName("실패 - 쿠키가 존재하지 않으면 INVALID_TOKEN 예외를 발생한다")
        void fail_INVALID_TOKEN() {
            // given
            Cookie[] cookies = new Cookie[]{};

            // when & then
            assertThatThrownBy(() -> CookieUtil.getTokenFromCookie(cookies, COOKIE_NAME_ACCESS_TOKEN))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(INVALID_TOKEN.getMessage());
        }
    }

    @Test
    @DisplayName("쿠키 무효화")
    void invalidate_Cookie() {
        // given

        // when
        ResponseCookie responseCookie = CookieUtil.invalidateCookie(COOKIE_NAME_ACCESS_TOKEN, COOKIE_PATH_ACCESS_TOKEN);

        // then
        assertThat(responseCookie.getValue()).isEmpty();
        assertThat(responseCookie.getPath()).isEqualTo("/");
        assertThat(responseCookie.getMaxAge()).isEqualTo(Duration.ZERO);
    }
}