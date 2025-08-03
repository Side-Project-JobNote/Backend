package com.jobnote.domain.user.service;

import com.jobnote.global.config.properties.AppProperties;
import com.jobnote.infra.mail.dto.MailMessageDto;
import com.jobnote.infra.mail.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserMailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private AppProperties appProperties;

    @Test
    @DisplayName("이메일 인증 링크를 검증한다")
    void verifyEmailVerificationLink() {
        // given
        final String to = "testUser@test.com";
        final String token = "testToken";
        final MockMailService mockMailService = new MockMailService(mailSender);
        final UserMailService userMailService = new UserMailService(mockMailService, appProperties);

        given(appProperties.baseUrl()).willReturn("http://localhost:8080");
        given(appProperties.emailVerificationPath()).willReturn("/api/v1/users/verify");

        // when
        userMailService.sendVerificationEmail(to, token);

        // then
        assertThat(mockMailService.getMailMessageDto().text()).contains("http://localhost:8080/api/v1/users/verify?token=" + token);
    }

    static class MockMailService extends MailService {

        private MailMessageDto mailMessageDto;

        public MockMailService(JavaMailSender mailSender) {
            super(mailSender);
        }

        public void sendMail(final MailMessageDto mailMessageDto) {
            this.mailMessageDto = mailMessageDto;
        }

        public MailMessageDto getMailMessageDto() {
            return mailMessageDto;
        }
    }
}