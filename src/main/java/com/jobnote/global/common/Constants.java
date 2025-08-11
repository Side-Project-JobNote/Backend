package com.jobnote.global.common;

import java.util.List;

public abstract class Constants {

    // uri
    public static final String URI_USER_REISSUE = "/api/v1/users/reissue";

    // claim
    public static final String CLAIM_NAME_TOKEN_TYPE = "token";
    public static final String CLAIM_NAME_EMAIL = "email";
    public static final String CLAIM_VALUE_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String CLAIM_VALUE_REFRESH_TOKEN = "REFRESH_TOKEN";

    // header
    public static final String CHARACTER_ENCODING = "UTF-8";

    // http request
    public static final String ATTRIBUTE_EXCEPTION = "exception";

    // cookie
    public static final String COOKIE_NAME_ACCESS_TOKEN = "access_token";
    public static final String COOKIE_NAME_REFRESH_TOKEN = "refresh_token";
    public static final String COOKIE_PATH_ACCESS_TOKEN = "/";
    public static final String COOKIE_PATH_REFRESH_TOKEN = URI_USER_REISSUE;

    // whitelist
    public static final String[] WHITELIST = {
            "/api/v1/users/signup",
            "/api/v1/verification-emails/signup/verify",
            "/api/v1/verification-emails/reset-password/**",
            "/login/**",
            "/oauth2/**",
            "/h2-console/**",
            "/error/**",
    };

    public static final List<String> TOKEN_FILTER_WHITELIST = List.of(
            "/api/v1/users/signup",
            "/api/v1/verification-emails/signup/verify",
            "/api/v1/verification-emails/reset-password",
            "/login",
            "/oauth2",
            "/h2-console",
            "/error"
    );

    public static final String[] ONLY_GUEST = {
            "/api/v1/users/signup/social",
    };
}
