package com.jobnote.domain.user.listener;

import com.jobnote.domain.user.domain.VerificationToken;
import com.jobnote.domain.user.dto.SignUpEvent;
import com.jobnote.global.config.properties.AppProperties;
import com.jobnote.infra.mail.dto.MailMessageDto;
import com.jobnote.infra.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class VerificationEmailEventListener {

    private final MailService mailService;
    private final AppProperties appProperties;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationEvent(final SignUpEvent signUpEvent) {
        MailMessageDto mailMessage = createMailMessageDto(signUpEvent.user().getEmail(), signUpEvent.verificationToken());
        mailService.sendMail(mailMessage);
    }

    private MailMessageDto createMailMessageDto(final String to, final VerificationToken verificationToken) {
        final String link = appProperties.baseUrl() + appProperties.emailVerificationPath() + "?token=" + verificationToken.getToken();
        final String subject = "JobNote 회원가입 이메일 인증";
        final String text = String.format("""
                JobNote를 이용해주셔서 감사합니다.
                아래 이메일 인증 링크를 클릭하여 회원가입을 완료해 주세요.
                감사합니다.
                %s
                """, link);
        return MailMessageDto.builder()
                .to(to)
                .subject(subject)
                .text(text)
                .build();
    }
}
