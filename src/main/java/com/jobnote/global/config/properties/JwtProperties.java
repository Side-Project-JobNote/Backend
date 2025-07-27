package com.jobnote.global.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final String secret;
    private final AccessTokenProperties accessToken;
    private final RefreshTokenProperties refreshToken;

    @Getter
    @RequiredArgsConstructor
    @ConfigurationProperties(prefix = "access-token")
    public static class AccessTokenProperties {
        private final long expirationTime;
    }

    @Getter
    @RequiredArgsConstructor
    @ConfigurationProperties(prefix = "refresh-token")
    public static class RefreshTokenProperties {
        private final long expirationTime;
    }
}
