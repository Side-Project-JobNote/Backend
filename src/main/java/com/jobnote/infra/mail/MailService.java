package com.jobnote.infra.mail;

import com.jobnote.global.exception.JobNoteException;
import com.jobnote.infra.mail.dto.MailMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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

    @Retryable(
            retryFor = {MailException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000)
    )
    @Async
    public void sendMail(final MailMessageDto mailMessageDto) {
        final SimpleMailMessage message = createMailMessage(mailMessageDto);

        try {
            mailSender.send(message);
        } catch (final MailException e) {
            log.warn("[메일 전송 오류]: {}", mailMessageDto);
            throw e;
        }
    }

    @Recover
    public void recover(final MailException e, final MailMessageDto mailMessageDto) {
        log.error("[이메일 전송 최종 실패]: {}", mailMessageDto, e);
        throw new JobNoteException(UNABLE_TO_SEND_MAIL);
    }

    private SimpleMailMessage createMailMessage(final MailMessageDto mailMessageDto) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailMessageDto.from());
        message.setTo(mailMessageDto.to());
        message.setSubject(mailMessageDto.subject());
        message.setText(mailMessageDto.text());
        return message;
    }
}
