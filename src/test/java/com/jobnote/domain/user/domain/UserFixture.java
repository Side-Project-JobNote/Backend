package com.jobnote.domain.user.domain;

public class UserFixture {

    public static User createMember(final Long userId, final String email, final String password, final String nickname) {
        return User.builder()
                .id(userId)
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(UserRole.MEMBER)
                .build();
    }

    public static User createWithdrawnMember(final Long userId, final String email, final String password, final String nickname) {
        return User.builder()
                .id(userId)
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(UserRole.MEMBER)
                .isDeleted(true)
                .build();
    }

    public static User createGuest(final Long userId, final String email, final String password, final String nickname) {
        return User.builder()
                .id(userId)
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(UserRole.GUEST)
                .build();
    }

    public static User createGuestKakao(final Long userId, final String email, final String socialEmail, final String socialId) {
        return User.builder()
                .id(userId)
                .email(email)
                .socialEmail(socialEmail)
                .socialId(socialId)
                .socialProvider(SocialProvider.KAKAO)
                .role(UserRole.GUEST)
                .build();
    }

    public static User createMemberKakao(final Long userId, final String email, final String socialEmail, final String socialId) {
        return User.builder()
                .id(userId)
                .email(email)
                .socialEmail(socialEmail)
                .socialId(socialId)
                .socialProvider(SocialProvider.KAKAO)
                .role(UserRole.MEMBER)
                .build();
    }
}
