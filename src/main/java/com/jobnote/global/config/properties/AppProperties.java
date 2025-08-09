package com.jobnote.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String baseUrl,
        EmailVerificationPathProperties emailVerificationPath
) {

    public record EmailVerificationPathProperties(
            String signUp,
            String resetPassword
    ) {
    }
}
