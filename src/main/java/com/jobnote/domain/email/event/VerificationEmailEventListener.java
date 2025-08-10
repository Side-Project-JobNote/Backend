package com.jobnote.domain.email.event;

import com.jobnote.domain.email.domain.VerificationEmailType;
import com.jobnote.global.config.properties.AppProperties;
import com.jobnote.infra.mail.MailService;
import com.jobnote.infra.mail.dto.MailMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class VerificationEmailEventListener {

    private final MailService mailService;
    private final AppProperties appProperties;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(final VerificationEmailEvent verificationEmailEvent) {
        final MailMessageDto mailMessageDto;
        if (VerificationEmailType.SIGN_UP.equals(verificationEmailEvent.type())) {
            mailMessageDto = createSignUpMailMessageDto(verificationEmailEvent);
        } else {
            mailMessageDto = createResetPasswordMailMessageDto(verificationEmailEvent);
        }

        mailService.sendMail(mailMessageDto);
    }

    private MailMessageDto createSignUpMailMessageDto(final VerificationEmailEvent event) {
        final String link = appProperties.baseUrl() + appProperties.emailVerificationPath().signUp() + "?token=" + event.token();
        final String subject = "JobNote 회원가입 이메일 인증";
        final String text = String.format("""
                JobNote를 이용해주셔서 감사합니다.
                아래 이메일 인증 링크를 클릭하여 회원가입을 완료해 주세요.
                감사합니다.
                %s
                """, link);

        return MailMessageDto.builder()
                .from(fromEmail)
                .to(event.toEmail())
                .subject(subject)
                .text(text)
                .build();
    }

    private MailMessageDto createResetPasswordMailMessageDto(final VerificationEmailEvent event) {
        final String link = appProperties.baseUrl() + appProperties.emailVerificationPath().resetPassword() + "?token=" + event.token();
        final String subject = "JobNote 비밀번호 재설정 이메일 인증";
        final String text = String.format("""
                JobNote를 이용해주셔서 감사합니다.
                아래 이메일 인증 링크를 클릭하여 비밀번호 재설정을 완료해 주세요.
                감사합니다.
                %s
                """, link);

        return MailMessageDto.builder()
                .from(fromEmail)
                .to(event.toEmail())
                .subject(subject)
                .text(text)
                .build();
    }
}
