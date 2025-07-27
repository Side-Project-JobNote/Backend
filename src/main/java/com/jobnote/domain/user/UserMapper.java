package com.jobnote.domain.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User toUser(final UserRequest request) {
        return User.builder()
                .loginId(request.loginId())
                .password(request.password())
                .email(request.email())
                .name(request.name())
                .avatarUrl(request.avatarUrl())
                .build();
    }

    public static UserResponse fromUser(final User user) {
        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getPassword(),
                user.getEmail(),
                user.getName(),
                user.getAvatarUrl()
        );
    }
}
