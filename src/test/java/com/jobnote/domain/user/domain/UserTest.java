package com.jobnote.domain.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("회원가입 시 Role은 GUEST(임시 회원)이다")
    void signUp_UserRole_GUEST() {
        // given

        // when
        User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.GUEST);
    }
}