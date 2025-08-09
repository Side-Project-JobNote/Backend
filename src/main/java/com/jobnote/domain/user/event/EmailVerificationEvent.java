package com.jobnote.domain.user.event;

import com.jobnote.global.config.properties.AppProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@Builder(access = AccessLevel.PRIVATE)
public record EmailVerificationEvent(
        String toEmail,
        String token,
        EmailType emailType
) {

    @Getter
    @RequiredArgsConstructor
    public enum EmailType {
        SIGN_UP("회원가입", AppProperties.EmailVerificationPathProperties::signUp),
        RESET_PASSWORD("비밀번호 재설정", AppProperties.EmailVerificationPathProperties::resetPassword),
        ;

        private final String title;
        private final Function<AppProperties.EmailVerificationPathProperties, String> pathFunction;

        public String getPath(final AppProperties.EmailVerificationPathProperties pathProperties) {
            return pathFunction.apply(pathProperties);
        }
    }

    public static EmailVerificationEvent signUp(final String toEmail, final String token) {
        return EmailVerificationEvent.of(toEmail, token)
                .emailType(EmailType.SIGN_UP)
                .build();
    }

    public static EmailVerificationEvent resetPassword(final String toEmail, final String token) {
        return EmailVerificationEvent.of(toEmail, token)
                .emailType(EmailType.RESET_PASSWORD)
                .build();
    }

    private static EmailVerificationEventBuilder of(final String toEmail, final String token) {
        return EmailVerificationEvent.builder()
                .toEmail(toEmail)
                .token(token);
    }
}
