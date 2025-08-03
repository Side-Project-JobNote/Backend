package com.jobnote.domain.user.service;

import com.jobnote.global.config.properties.AppProperties;
import com.jobnote.infra.mail.dto.MailMessageDto;
import com.jobnote.infra.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserMailService {

    private final MailService mailService;
    private final AppProperties appProperties;

    public void sendVerificationEmail(final String to, final String token) {
        MailMessageDto mailMessage = createVerificationEmailMessageDto(to, token);
        mailService.sendMail(mailMessage);
    }

    private MailMessageDto createVerificationEmailMessageDto(final String to, final String token) {
        final String link = appProperties.baseUrl() + appProperties.emailVerificationPath() + "?token=" + token;
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
