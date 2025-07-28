package com.jobnote.domain.user.service;

import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.dto.UserRequest;
import com.jobnote.domain.user.dto.UserResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User toUser(final UserRequest request) {
        return User.builder()
                .email(request.email())
                .password(request.password())
                .nickname(request.nickname())
                .avatarUrl(request.avatarUrl())
                .build();
    }

    public static UserResponse fromUser(final User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getAvatarUrl()
        );
    }
}
