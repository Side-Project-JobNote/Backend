package com.jobnote.global.common;

import java.util.Set;

public abstract class Constants {

    public static final String BEARER = "Bearer ";

    // claim
    public static final String CLAIM_NAME_TOKEN_TYPE = "token";
    public static final String CLAIM_NAME_EMAIL = "email";
    public static final String CLAIM_VALUE_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String CLAIM_VALUE_REFRESH_TOKEN = "REFRESH_TOKEN";

    // header
    public static final String CHARACTER_ENCODING = "UTF-8";

    // cookie
    public static final String COOKIE_NAME_ACCESS_TOKEN = "access_token";
    public static final String COOKIE_NAME_REFRESH_TOKEN = "refresh_token";
    public static final String COOKIE_PATH_ACCESS_TOKEN = "/";
    public static final String COOKIE_PATH_REFRESH_TOKEN = "/";

    // whitelist
    public static final String[] WHITELIST = {
            "/api/v1/users/signup/**",
            "/api/v1/users/login",
            "/api/v1/users/issue/code",
            "/api/v1/users/reissue",
            "/api/v1/users/reset-password",
            "/api/v1/verification-emails/**",
            "/oauth2/**",
            "/h2-console/**",
            "/error/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
    };

    public static final Set<String> TOKEN_FILTER_WHITELIST = Set.of(
            "/api/v1/users/signup",
            "/api/v1/users/signup/social",
            "/api/v1/users/login",
            "/api/v1/users/issue/code",
            "/api/v1/users/reissue",
            "/api/v1/users/reset-password",
            "/api/v1/verification-emails",
            "/api/v1/verification-emails/signup/verify",
            "/api/v1/verification-emails/reset-password/verify",
            "/oauth2/authorization/naver",
            "/oauth2/authorization/kakao",
            "/oauth2/authorization/google"
    );
}
