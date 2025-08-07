package com.jobnote.domain.document.dto;

import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.domain.DocumentType;
import com.jobnote.domain.user.domain.User;

public record DocumentRequest(
        String fileName,
        String fileKey,
        DocumentType fileType,
        Long fileSize
) {
    public Document toEntity(final User user) {
        return Document.builder()
                .user(user)
                .documentType(fileType)
                .title(fileName)
                .build();
    }
}
