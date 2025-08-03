package com.jobnote.infra.mail.dto;

import lombok.Builder;

@Builder
public record MailMessageDto(
        String to,
        String subject,
        String text
) {
}
