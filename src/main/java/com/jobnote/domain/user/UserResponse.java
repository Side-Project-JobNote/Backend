package com.jobnote.domain.user;

public record UserResponse(
        Long id,
        String loginId,
        String password,
        String email,
        String name,
        String avatarUrl
) {
}