package com.jobnote.auth;

import com.jobnote.JobnoteApplicationTests;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.dto.UserLoginRequest;
import com.jobnote.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import static com.jobnote.global.common.Constants.COOKIE_NAME_ACCESS_TOKEN;
import static com.jobnote.global.common.Constants.COOKIE_NAME_REFRESH_TOKEN;
import static com.jobnote.global.common.ResponseCode.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginFilterApplicationTest extends JobnoteApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String LOGIN_URI = "/api/v1/users/login";

    @Nested
    @DisplayName("로그인")
    class Login {
        @Nested
        @DisplayName("성공")
        class Success {
            final String email = "testCorrectEmail@test.com";
            final String password = "testCorrectPassword";
            final String nickname = "testNickname";

            @Test
            @DisplayName("GUEST는 토큰 미발급 & PENDING_EMAIL_VERIFICATION 예외 발생")
            void success_GUEST_NotIssueToken_PENDING_EMAIL_VERIFICATION() throws Exception {
                // given
                createGuestUser(email, password, nickname);
                UserLoginRequest request = new UserLoginRequest(email, password);

                // when
                ResultActions resultActions = mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // then
                resultActions
                        .andExpect(status().isForbidden())
                        .andExpect(cookie().doesNotExist(COOKIE_NAME_ACCESS_TOKEN))
                        .andExpect(cookie().doesNotExist(COOKIE_NAME_REFRESH_TOKEN))
                        .andExpect(jsonPath("$.data").isEmpty())
                        .andExpect(jsonPath("$.code").value(PENDING_EMAIL_VERIFICATION.getCode()));
            }

            @Test
            @DisplayName("MEMBER는 토큰 발급")
            void success_NotGUEST_IssueToken() throws Exception {
                // given
                createMemberUser(email, password, nickname);
                UserLoginRequest request = new UserLoginRequest(email, password);

                // when
                ResultActions resultActions = mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // then
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(cookie().exists(COOKIE_NAME_ACCESS_TOKEN))
                        .andExpect(cookie().exists(COOKIE_NAME_REFRESH_TOKEN));
            }
        }

        @Nested
        @DisplayName("실패 - 토큰 미발급")
        class Fail {
            final String correctEmail = "testCorrectEmail@test.com";
            final String correctPassword = "testCorrectPassword";
            final String nickname = "testNickname";

            @Test
            @DisplayName("잘못된 이메일")
            void fail_NotIssueToken_InvalidEmail() throws Exception {
                // given
                final String wrongEmail = "testWrongEmail@test.com";
                createGuestUser(correctEmail, correctPassword, nickname);
                UserLoginRequest request = new UserLoginRequest(wrongEmail, correctPassword);

                // when
                ResultActions resultActions = mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // then
                resultActions
                        .andExpect(status().isUnauthorized())
                        .andExpect(cookie().doesNotExist(COOKIE_NAME_ACCESS_TOKEN))
                        .andExpect(cookie().doesNotExist(COOKIE_NAME_REFRESH_TOKEN))
                        .andExpect(jsonPath("$.data").isEmpty())
                        .andExpect(jsonPath("$.code").value(INVALID_USERNAME_PASSWORD.getCode()));
            }

            @Test
            @DisplayName("잘못된 비밀번호")
            void fail_NotIssueToken_InvalidPassword() throws Exception {
                // given
                final String wrongPassword = "testWrongPassword";
                createGuestUser(correctEmail, correctPassword, nickname);
                UserLoginRequest request = new UserLoginRequest(correctEmail, wrongPassword);

                // when
                ResultActions resultActions = mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // then
                resultActions
                        .andExpect(status().isUnauthorized())
                        .andExpect(cookie().doesNotExist(COOKIE_NAME_ACCESS_TOKEN))
                        .andExpect(cookie().doesNotExist(COOKIE_NAME_REFRESH_TOKEN))
                        .andExpect(jsonPath("$.data").isEmpty())
                        .andExpect(jsonPath("$.code").value(INVALID_USERNAME_PASSWORD.getCode()));
            }
        }
    }

    private void createGuestUser(final String email, final String password, final String nickname) {
        signUp(email, password, nickname);
    }

    private void createMemberUser(final String email, final String password, final String nickname) {
        User savedUser = signUp(email, password, nickname);
        savedUser.accept();
    }

    private User signUp(final String email, final String password, final String nickname) {
        return userRepository.save(User.signUp(email, passwordEncoder.encode(password), nickname));
    }
}