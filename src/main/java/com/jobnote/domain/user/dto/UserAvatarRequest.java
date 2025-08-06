package com.jobnote.domain.user.dto;

import lombok.Builder;

@Builder
public record UserAvatarRequest(
        String avatarUrl
) {
}
