package com.jobnote.domain.user.dto;

public record UserResponse(
        Long id,
        String email,
        String password,
        String nickname,
        String avatarUrl
) {
}