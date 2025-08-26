package com.jobnote.domain.user.service;

import com.jobnote.ServiceUnitTest;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.UserFixture;
import com.jobnote.domain.user.dto.UserLoginRequest;
import com.jobnote.global.exception.JobNoteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.jobnote.global.common.ResponseCode.INVALID_USERNAME_PASSWORD;
import static com.jobnote.global.common.ResponseCode.PENDING_EMAIL_VERIFICATION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class LoginServiceTest extends ServiceUnitTest {

    @Mock
    private Authentication authentication;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthTokenService authTokenService;

    @InjectMocks
    private LoginService loginService;

    @Nested
    @DisplayName("로그인")
    class Login {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            final String email = "testEmail@test.com";
            final String password = "testPassword";
            final UserLoginRequest request = new UserLoginRequest(email, password);
            final User user = UserFixture.createMember(email, password, "testNickname");
            final CustomUserDetails principal = new CustomUserDetails(user);

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(principal);

            // when
            loginService.login(request);

            // then
            then(authTokenService).should().saveAndGetToken(any());
        }

        @Test
        @DisplayName("실패 - 이메일 인증이 완료되지 않으면 예외를 발생한다.")
        void fail_PendingEmailVerification_ThrowsException() {
            // given
            final String email = "testEmail@test.com";
            final String password = "testPassword";
            final UserLoginRequest request = new UserLoginRequest(email, password);
            final User user = UserFixture.createGuest(email, password, "testNickname");
            final CustomUserDetails principal = new CustomUserDetails(user);

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
            given(authentication.getPrincipal()).willReturn(principal);

            // when & then
            assertThatThrownBy(() -> loginService.login(request))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(PENDING_EMAIL_VERIFICATION.getMessage());

            then(authTokenService).should(never()).saveAndGetToken(any(Long.class));
        }

        @Test
        @DisplayName("실패 - 이메일이 존재하지 않으면 예외를 발생한다.")
        void fail_UsernameNotFound_ThrowsException() {
            // given
            final String email = "testEmail@test.com";
            final String password = "testPassword";
            final UserLoginRequest request = new UserLoginRequest(email, password);

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willThrow(UsernameNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> loginService.login(request))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(INVALID_USERNAME_PASSWORD.getMessage());

            then(authentication).should(never()).getPrincipal();
            then(authTokenService).should(never()).saveAndGetToken(any(Long.class));
        }

        @Test
        @DisplayName("실패 - 비밀번호가 틀리면 예외를 발생한다.")
        void fail_BadCredentials_ThrowsException() {
            // given
            final String email = "testEmail@test.com";
            final String password = "testPassword";
            final UserLoginRequest request = new UserLoginRequest(email, password);

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willThrow(BadCredentialsException.class);

            // when & then
            assertThatThrownBy(() -> loginService.login(request))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(INVALID_USERNAME_PASSWORD.getMessage());

            then(authentication).should(never()).getPrincipal();
            then(authTokenService).should(never()).saveAndGetToken(any(Long.class));
        }
    }
}