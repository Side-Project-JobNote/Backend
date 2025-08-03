package com.jobnote.domain.user.service;

import com.jobnote.ServiceUnitTest;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.UserRole;
import com.jobnote.domain.user.domain.VerificationToken;
import com.jobnote.domain.user.dto.SignUpEvent;
import com.jobnote.domain.user.dto.UserSignUpRequest;
import com.jobnote.domain.user.repository.UserRepository;
import com.jobnote.domain.user.repository.VerificationTokenRepository;
import com.jobnote.global.exception.JobNoteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.jobnote.global.common.ResponseCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

class UserServiceTest extends ServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("회원가입")
    class SignUp {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            final UserSignUpRequest request = new UserSignUpRequest("testEmail@test.com", "testPassword", "testNickname");
            final User user = mock(User.class);
            final VerificationToken verificationToken = mock(VerificationToken.class);
            given(userRepository.existsByEmail(request.email())).willReturn(false);
            given(userRepository.existsByNickname(request.nickname())).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(user);
            given(verificationTokenRepository.save(any(VerificationToken.class))).willReturn(verificationToken);
            willDoNothing().given(eventPublisher).publishEvent(any(SignUpEvent.class));

            // when
            userService.signUp(request, LocalDateTime.now());

            // then
            then(userRepository).should().existsByNickname(request.nickname());
            then(userRepository).should().save(any(User.class));
            then(verificationTokenRepository).should().save(any(VerificationToken.class));
            then(eventPublisher).should().publishEvent(any(SignUpEvent.class));
        }

        @Test
        @DisplayName("실패 - 이메일 중복")
        void fail_DuplicatedEmail() {
            // given
            UserSignUpRequest request = new UserSignUpRequest("testEmail@test.com", "testPassword", "testNickname");
            given(userRepository.existsByEmail(request.email())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.signUp(request, LocalDateTime.now()))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(DUPLICATED_USER_EMAIL.getMessage());

            then(userRepository).should().existsByEmail(request.email());
            then(userRepository).should(never()).save(any(User.class));
            then(verificationTokenRepository).should(never()).save(any(VerificationToken.class));
            then(eventPublisher).should(never()).publishEvent(any(SignUpEvent.class));
        }

        @Test
        @DisplayName("실패 - 닉네임 중복")
        void fail_DuplicatedNickname() {
            // given
            UserSignUpRequest request = new UserSignUpRequest("testEmail@test.com", "testPassword", "testNickname");
            given(userRepository.existsByNickname(request.nickname())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.signUp(request, LocalDateTime.now()))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(DUPLICATED_USER_NICKNAME.getMessage());

            then(userRepository).should().existsByNickname(request.nickname());
            then(userRepository).should(never()).save(any(User.class));
            then(verificationTokenRepository).should(never()).save(any(VerificationToken.class));
            then(eventPublisher).should(never()).publishEvent(any(SignUpEvent.class));
        }
    }

    @Nested
    @DisplayName("이메일 인증")
    class VerifyEmail {
        @Test
        @DisplayName("성공 - 회원의 Role은 MEMBER가 된다")
        void success() {
            // given
            final String token = "testToken";
            final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 8, 3, 15, 26, 0);
            final VerificationToken verificationToken = VerificationToken.create(token, user, expiryDate);

            given(verificationTokenRepository.findByToken(token)).willReturn(Optional.of(verificationToken));

            // when
            userService.verifyEmail(token, expiryDate.minusDays(1));

            // then
            then(verificationTokenRepository).should().findByToken(token);
            assertThat(user.getRole()).isEqualTo(UserRole.MEMBER);
        }

        @Test
        @DisplayName("실패 - 검증 토큰이 DB에 존재하지 않으면 NOT_FOUND_VERIFICATION_TOKEN 예외를 발생한다")
        void fail_NotFound_VerificationToken() {
            // given
            final String token = "testToken";
            final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 8, 3, 15, 26, 0);

            given(verificationTokenRepository.findByToken(token)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.verifyEmail(token, expiryDate.minusDays(1)))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(NOT_FOUND_VERIFICATION_TOKEN.getMessage());

            then(verificationTokenRepository).should().findByToken(token);
            assertThat(user.getRole()).isEqualTo(UserRole.GUEST);
        }

        @Test
        @DisplayName("실패 - 검증 토큰이 만료되면 EXPIRED_VERIFICATION_TOKEN 예외를 발생한다")
        void fail_Expired_VerificationToken() {
            // given
            final String token = "testToken";
            final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 8, 3, 15, 26, 0);
            final VerificationToken verificationToken = VerificationToken.create(token, user, expiryDate);

            given(verificationTokenRepository.findByToken(token)).willReturn(Optional.of(verificationToken));

            // when & then
            assertThatThrownBy(() -> userService.verifyEmail(token, expiryDate.plusDays(1)))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(EXPIRED_VERIFICATION_TOKEN.getMessage());

            then(verificationTokenRepository).should().findByToken(token);
            assertThat(user.getRole()).isEqualTo(UserRole.GUEST);
        }
    }
}