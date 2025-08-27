package com.jobnote.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "frontend")
public record FrontendProperties(
        String baseUrl,
        String mainPage,
        String socialSignUpPage,
        String loginFailPage,
        String resetPasswordPage,
        String localUrl,
        String localSecureUrl
) {

}
