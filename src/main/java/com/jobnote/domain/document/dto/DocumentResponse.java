package com.jobnote.domain.document.dto;

import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.domain.DocumentType;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDate;


@Builder(access = AccessLevel.PRIVATE)
public record DocumentResponse(
        Long id,
        DocumentType type,
        String title,
        LocalDate lastModifiedDate
) {
    public static DocumentResponse from(final Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .type(document.getType())
                .title(document.getTitle())
                .lastModifiedDate(document.getModifiedDate().toLocalDate())
                .build();
    }
}
