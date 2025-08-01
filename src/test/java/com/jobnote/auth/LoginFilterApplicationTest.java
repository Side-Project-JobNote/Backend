package com.jobnote.auth;

import com.jobnote.JobnoteApplicationTests;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.dto.UserLoginRequest;
import com.jobnote.domain.user.repository.RefreshTokenRepository;
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
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User savedUser;

    private final String LOGIN_URI = "/login";
    private final String CORRECT_EMAIL = "testCorrectEmail@test.com";
    private final String CORRECT_PASSWORD = "testCorrectPassword";

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(User.signUp(CORRECT_EMAIL, passwordEncoder.encode(CORRECT_PASSWORD), "testNickname"));
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("로그인")
    class Login {
        @Test
        @DisplayName("성공 - 토큰 발급")
        void success_IssueToken() throws Exception {
            // given
            UserLoginRequest request = new UserLoginRequest(CORRECT_EMAIL, CORRECT_PASSWORD);

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

        @Nested
        @DisplayName("실패 - 토큰 미발급")
        class Fail {

            @Test
            @DisplayName("잘못된 이메일")
            void fail_NotIssueToken_InvalidEmail() throws Exception {
                // given
                final String WRONG_EMAIL = "testWrongEmail@test.com";
                UserLoginRequest request = new UserLoginRequest(WRONG_EMAIL, CORRECT_PASSWORD);

                // when
                ResultActions resultActions = mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // then
                resultActions
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.data").isEmpty())
                        .andExpect(jsonPath("$.code").value(INVALID_USERNAME_PASSWORD.getCode()));
            }

            @Test
            @DisplayName("잘못된 비밀번호")
            void fail_NotIssueToken_InvalidPassword() throws Exception {
                // given
                final String WRONG_PASSWORD = "testWrongPassword ";
                UserLoginRequest request = new UserLoginRequest(CORRECT_EMAIL, WRONG_PASSWORD);

                // when
                ResultActions resultActions = mockMvc.perform(post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                // then
                resultActions
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.data").isEmpty())
                        .andExpect(jsonPath("$.code").value(INVALID_USERNAME_PASSWORD.getCode()));
            }
        }
    }
}