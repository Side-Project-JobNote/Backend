package com.jobnote.domain.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User toUser(final UserRequest request) {
        return User.builder()
                .email(request.email())
                .password(request.password())
                .name(request.name())
                .avatarUrl(request.avatarUrl())
                .build();
    }

    public static UserResponse fromUser(final User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getAvatarUrl()
        );
    }
}
