package com.jobnote.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDateTime;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String baseUrl,
        String emailVerificationPath,
        LocalDateTime emailVerificationExpiryTime
) {
}
