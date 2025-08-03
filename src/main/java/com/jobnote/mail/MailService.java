package com.jobnote.mail;

import com.jobnote.global.exception.JobNoteException;
import com.jobnote.mail.dto.MailMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.*;

import static com.jobnote.global.common.ResponseCode.UNABLE_TO_SEND_MAIL;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Async
    public void sendMail(final MailMessageDto mailMessageDto) {
        SimpleMailMessage message = createMailMessage(mailMessageDto);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error("이메일 전송 오류 ", e);
            throw new JobNoteException(UNABLE_TO_SEND_MAIL);
        }
    }

    private SimpleMailMessage createMailMessage(final MailMessageDto mailMessageDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(mailMessageDto.to());
        message.setSubject(mailMessageDto.subject());
        message.setText(mailMessageDto.text());
        return message;
    }
}
