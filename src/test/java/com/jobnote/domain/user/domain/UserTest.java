package com.jobnote.domain.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");

        // when
        user.accept();

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.MEMBER);
    }

    @Test
    @DisplayName("소셜 로그인 회원을 accept시 닉네임 업데이트와 함께 Role은 MEMBER가 된다.")
    void socialSignUp_accept() {
        // given
        final User user = User.socialSignUp("testEmail@test.com", "testSocailEmail@test.com", SocialProvider.GOOGLE, "testSocialId");
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
        final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
        final User socialUser = User.socialSignUp("testEmail@test.com", "testSocailEmail@test.com", SocialProvider.GOOGLE, "testSocialId");
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
        final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
        final User socialUser = User.socialSignUp("testEmail@test.com", "testSocailEmail@test.com", SocialProvider.GOOGLE, "testSocialId");
        final String updatedNickname = "updatedNickname";

        // when
        user.updateNickname(updatedNickname);
        socialUser.updateNickname(updatedNickname);

        // then
        assertThat(user.getNickname()).isEqualTo(updatedNickname);
        assertThat(socialUser.getNickname()).isEqualTo(updatedNickname);
    }

}