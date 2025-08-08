package com.jobnote.domain.user.event;

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
public class SignUpEventListener {

    private final MailService mailService;
    private final AppProperties appProperties;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(final SignUpEvent signUpEvent) {
        final MailMessageDto mailMessageDto = createMailMessageDto(signUpEvent.toEmail(), signUpEvent.verificationToken());
        mailService.sendMail(mailMessageDto);
    }

    private MailMessageDto createMailMessageDto(final String email, final String verificationToken) {
        final String link = appProperties.baseUrl() + appProperties.emailVerificationPath() + "?token=" + verificationToken;
        final String subject = "JobNote 회원가입 이메일 인증";
        final String text = String.format("""
                JobNote를 이용해주셔서 감사합니다.
                아래 이메일 인증 링크를 클릭하여 회원가입을 완료해 주세요.
                감사합니다.
                %s
                """, link);

        return MailMessageDto.builder()
                .from(fromEmail)
                .to(email)
                .subject(subject)
                .text(text)
                .build();
    }
}
