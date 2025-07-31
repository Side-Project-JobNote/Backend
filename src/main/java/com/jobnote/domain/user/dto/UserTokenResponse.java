package com.jobnote.domain.user.dto;

import com.jobnote.auth.token.Token;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UserTokenResponse(
        Long userId,
        String accessToken,
        String refreshToken
) {

    public static UserTokenResponse of(final Long userId, final Token token) {
        return UserTokenResponse.builder()
                .userId(userId)
                .accessToken(token.accessToken())
                .refreshToken(token.refreshToken())
                .build();
    }
}
