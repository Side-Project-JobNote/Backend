package com.jobnote.domain.user.service;

import com.jobnote.ServiceUnitTest;
import com.jobnote.domain.email.domain.VerificationEmailType;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.UserRole;
import com.jobnote.domain.user.dto.*;
import com.jobnote.domain.email.domain.VerificationEmail;
import com.jobnote.domain.user.repository.UserRepository;
import com.jobnote.domain.email.service.VerificationEmailService;
import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.exception.JobNoteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
    private VerificationEmailService verificationEmailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("회원가입")
    class SignUp {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            final String email = "testEmail@test.com";
            final String nickname = "testNickname";
            final String password = "testPassword";
            final UserSignUpRequest request = new UserSignUpRequest(email, password, nickname);
            final User user = User.signUp(email, password, nickname);
            final LocalDateTime emailVerificationExpiryDate = LocalDateTime.of(2025, 8, 6, 11, 31);

            given(userRepository.existsByEmail(email)).willReturn(false);
            given(userRepository.existsByNickname(nickname)).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(user);
            willDoNothing().given(verificationEmailService).send(user, emailVerificationExpiryDate, VerificationEmailType.SIGN_UP);

            // when
            userService.signUp(request, emailVerificationExpiryDate);

            // then
            then(userRepository).should().existsByEmail(email);
            then(userRepository).should().existsByNickname(nickname);
            then(userRepository).should().save(any(User.class));
            then(verificationEmailService).should().send(user, emailVerificationExpiryDate, VerificationEmailType.SIGN_UP);
        }

        @Test
        @DisplayName("실패 - 이메일 중복")
        void fail_DuplicatedEmail() {
            // given
            final UserSignUpRequest request = new UserSignUpRequest("testEmail@test.com", "testPassword", "testNickname");
            given(userRepository.existsByEmail(request.email())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.signUp(request, LocalDateTime.now()))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(DUPLICATED_USER_EMAIL.getMessage());

            then(userRepository).should().existsByEmail(request.email());
            then(userRepository).should(never()).save(any(User.class));
            then(verificationEmailService).should(never()).send(any(User.class), any(LocalDateTime.class), any(VerificationEmailType.class));
        }

        @Test
        @DisplayName("실패 - 닉네임 중복")
        void fail_DuplicatedNickname() {
            // given
            final UserSignUpRequest request = new UserSignUpRequest("testEmail@test.com", "testPassword", "testNickname");
            given(userRepository.existsByNickname(request.nickname())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.signUp(request, LocalDateTime.now()))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(DUPLICATED_USER_NICKNAME.getMessage());

            then(userRepository).should().existsByNickname(request.nickname());
            then(userRepository).should(never()).save(any(User.class));
            then(verificationEmailService).should(never()).send(any(User.class), any(LocalDateTime.class), any(VerificationEmailType.class));
        }
    }

    @Nested
    @DisplayName("프로필 이미지 업데이트")
    class UpdateAvatar {
        @Test
        @DisplayName("성공 - 프로필 이미지만 업데이트된다.")
        void success() {
            // given
            final Long userId = 1L;
            final String existingEmail = "testEmail@test.com";
            final String existingPassword = "testPassword";
            final String existingNickname = "testNickname";
            final User user = User.signUp(existingEmail, existingPassword, existingNickname);

            final String updatedAvatarUrl = "updatedAvatarUrl";
            final UserAvatarRequest request = UserAvatarRequest.builder().avatarUrl(updatedAvatarUrl).build();

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            final UserProfileResponse result = userService.updateAvatar(userId, request);

            // then
            assertThat(result.avatarUrl()).isEqualTo(updatedAvatarUrl);
            assertThat(result.email()).isEqualTo(existingEmail);
            assertThat(result.nickname()).isEqualTo(existingNickname);
        }
    }

    @Nested
    @DisplayName("닉네임 업데이트")
    class UpdateNickname {
        @Test
        @DisplayName("성공 - 닉네임만 업데이트된다.")
        void success() {
            // given
            final Long userId = 1L;
            final String existingEmail = "testEmail@test.com";
            final String existingPassword = "testPassword";
            final String existingNickname = "testNickname";
            final User user = User.signUp(existingEmail, existingPassword, existingNickname);

            final String updatedNickname = "updatedNickname";
            final UserNicknameRequest request = UserNicknameRequest.builder().nickname(updatedNickname).build();

            given(userRepository.existsByNickname(updatedNickname)).willReturn(false);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            final UserProfileResponse result = userService.updateNickname(userId, request);

            // then
            assertThat(result.email()).isEqualTo(existingEmail);
            assertThat(result.nickname()).isEqualTo(updatedNickname);
        }

        @Test
        @DisplayName("실패 - 이미 존재하는 닉네임이면 예외가 발생한다.")
        void fail_DuplicatedNickname() {
            // given
            final Long userId = 1L;
            final String updatedNickname = "updatedNickname";
            final UserNicknameRequest request = UserNicknameRequest.builder().nickname(updatedNickname).build();

            given(userRepository.existsByNickname(updatedNickname)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.updateNickname(userId, request))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(DUPLICATED_USER_NICKNAME.getMessage());
            then(userRepository).should(never()).findById(userId);
        }
    }

    @Nested
    @DisplayName("id로 회원 조회")
    class GetUser {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            final Long userId = 1L;
            final String email = "testEmail@test.com";
            final String password = "testPassword";
            final String nickname = "testNickname";
            final User user = User.signUp(email, password, nickname);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            final User result = userService.getUserById(userId);

            // then
            assertThat(result).isEqualTo(user);
        }

        @Test
        @DisplayName("실패 - 회원이 존재하지 않는다.")
        void fail_NotExistingUser() {
            // given
            final Long userId = 1L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserById(userId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(ResponseCode.NOT_FOUND_USER.getMessage());
        }
    }

    @Nested
    @DisplayName("이메일 인증")
    class EmailVerification {
        @Test
        @DisplayName("성공 - 회원의 Role은 MEMBER가 된다.")
        void success() {
            // given
            final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final LocalDateTime currentDate = LocalDateTime.of(2025, 7, 29, 12, 0);
            final String token = UUID.randomUUID().toString();
            final VerificationEmail verificationEmail = VerificationEmail.create(token, user, expiryDate);

            given(verificationEmailService.verify(token, currentDate)).willReturn(verificationEmail);

            // when
            userService.verifySignUp(token, currentDate);

            // then
            assertThat(user.getRole()).isEqualTo(UserRole.MEMBER);
        }
    }

    @Test
    @DisplayName("비밀번호 재설정")
    void resetPassword() {
        // given
        final String newPassword = "testNewPassword";
        final String newEncodedPassword = "testNewEncodedPassword";
        final String token = "testToken";
        final UserResetPasswordRequest request = new UserResetPasswordRequest(newPassword);
        final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
        final VerificationEmail verificationEmail = VerificationEmail.create(token, user, LocalDateTime.of(2025, 8, 6, 11, 31));
        verificationEmail.verify();

        given(verificationEmailService.validateVerified(token)).willReturn(verificationEmail);
        given(passwordEncoder.encode(newPassword)).willReturn(newEncodedPassword);

        // when
        userService.resetPassword(request, token);

        // then
        assertThat(user.getPassword()).isEqualTo(newEncodedPassword);
    }
}