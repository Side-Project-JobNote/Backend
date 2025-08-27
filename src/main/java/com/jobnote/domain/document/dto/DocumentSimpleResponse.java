package com.jobnote.domain.document.dto;

import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.domain.DocumentType;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record DocumentSimpleResponse(
        Long id,
        DocumentType type,
        String title
) {
    public static DocumentSimpleResponse from(final Document document) {
        return DocumentSimpleResponse.builder()
                .id(document.getId())
                .type(document.getType())
                .title(document.getTitle())
                .build();
    }
}
