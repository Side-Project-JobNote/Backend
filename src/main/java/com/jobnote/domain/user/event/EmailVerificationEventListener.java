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
public class EmailVerificationEventListener {

    private final MailService mailService;
    private final AppProperties appProperties;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(final EmailVerificationEvent emailVerificationEvent) {
        final MailMessageDto mailMessageDto = createMailMessageDto(emailVerificationEvent);
        System.out.println(mailMessageDto);
        mailService.sendMail(mailMessageDto);
    }

    private MailMessageDto createMailMessageDto(final EmailVerificationEvent event) {
        final String link = appProperties.baseUrl() + event.emailType().getPath(appProperties.emailVerificationPath()) + "?token=" + event.token();
        final String subject = String.format("JobNote %s 이메일 인증", event.emailType().getTitle());
        final String text = String.format("""
                JobNote를 이용해주셔서 감사합니다.
                아래 이메일 인증 링크를 클릭하여 %s을 완료해 주세요.
                감사합니다.
                %s
                """, event.emailType().getTitle(), link);

        return MailMessageDto.builder()
                .from(fromEmail)
                .to(event.toEmail())
                .subject(subject)
                .text(text)
                .build();
    }
}
