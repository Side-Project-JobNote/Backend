package com.jobnote.domain.user.domain;

import com.jobnote.global.exception.JobNoteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.jobnote.global.common.ResponseCode.USER_ALREADY_WITHDRAWN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("회원가입 시 Role은 GUEST(임시 회원)이다.")
    void signUp_UserRole_GUEST() {
        // given

        // when
        final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.GUEST);
    }

    @Test
    @DisplayName("자체 로그인 회원을 accept시 Role은 MEMBER가 된다.")
    void signUp_accept() {
        // given
        final User user = UserFixture.createGuest(1L, "testEmail@test.com", "testPassword", "testNickname");

        // when
        user.accept();

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.MEMBER);
    }

    @Test
    @DisplayName("소셜 로그인 회원을 accept시 닉네임 업데이트와 함께 Role은 MEMBER가 된다.")
    void socialSignUp_accept() {
        // given
        final User user = UserFixture.createGuestKakao(1L, "testEmail@test.com", "testSocailEmail@test.com", "testSocialId");
        final String nickname = "testNickname";

        // when
        user.acceptSocial(nickname);

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.MEMBER);
        assertThat(user.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("회원의 프로필 이미지를 수정한다")
    void updateAvatar() {
        // given
        final User user = UserFixture.createMember(1L, "testEmail@test.com", "testPassword", "testNickname");
        final User socialUser = UserFixture.createMemberKakao(1L, "testEmail@test.com", "testSocailEmail@test.com", "testSocialId");
        final String updatedAvatarUrl = "updatedAvatarUrl";

        // when
        user.updateAvatar(updatedAvatarUrl);
        socialUser.updateAvatar(updatedAvatarUrl);

        // then
        assertThat(user.getAvatarUrl()).isEqualTo(updatedAvatarUrl);
        assertThat(socialUser.getAvatarUrl()).isEqualTo(updatedAvatarUrl);
    }

    @Test
    @DisplayName("회원의 닉네임을 수정한다")
    void updateNickname() {
        // given
        final User user = UserFixture.createMember(1L, "testEmail@test.com", "testPassword", "testNickname");
        final User socialUser = UserFixture.createMemberKakao(1L, "testEmail@test.com", "testSocailEmail@test.com", "testSocialId");
        final String updatedNickname = "updatedNickname";

        // when
        user.updateNickname(updatedNickname);
        socialUser.updateNickname(updatedNickname);

        // then
        assertThat(user.getNickname()).isEqualTo(updatedNickname);
        assertThat(socialUser.getNickname()).isEqualTo(updatedNickname);
    }

    @Test
    @DisplayName("비밀번호를 재설정한다")
    void resetPassword() {
        // given
        final String existingPassword = "testPassword";
        final String newPassword = "testNewPassword";
        final User user = UserFixture.createMember(1L, "testEmail@test.com", existingPassword, "testNickname");

        // when
        user.resetPassword(newPassword);

        // then
        assertThat(user.getPassword()).isEqualTo(newPassword);
        assertThat(user.getPassword()).isNotEqualTo(existingPassword);
    }

    @Nested
    @DisplayName("회원탈퇴")
    class Withdraw {
        @Test
        @DisplayName("성공 - 회원탈퇴하지 않은 회원의 요청은 성공한다.")
        void success() {
            // given
            final User user = UserFixture.createMember(1L, "testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime deletedDate = LocalDateTime.of(2025, 8, 11, 21, 30, 0);

            // when
            user.withdraw(deletedDate);

            // then
            assertThat(user.isDeleted()).isTrue();
            assertThat(user.getDeletedDate()).isEqualTo(deletedDate);
        }

        @Test
        @DisplayName("실패 - 이미 탈퇴한 회원의 요청은 예외를 발생한다")
        void fail_AlreadyWithdrawn_ThrowsException() {
            // given
            final User user = UserFixture.createWithdrawnMember(1L, "testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime deletedDate = LocalDateTime.of(2025, 8, 11, 21, 30, 0);

            // when & then
            assertThatThrownBy(() -> user.withdraw(deletedDate))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(USER_ALREADY_WITHDRAWN.getMessage());
        }
    }
}