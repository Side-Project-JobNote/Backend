package com.jobnote.domain.user.domain;

public class UserFixture {

    public static User createMember(final String email, final String password, final String nickname) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(UserRole.MEMBER)
                .build();
    }
}
