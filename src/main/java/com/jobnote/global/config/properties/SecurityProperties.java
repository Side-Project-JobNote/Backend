package com.jobnote.global.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.security")
public record SecurityProperties(
        List<String> whitelist
) {
}
