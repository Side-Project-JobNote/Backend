package com.jobnote.domain.document.dto;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record DocumentListResponse(
        List<DocumentResponse> documents
) {
    public static DocumentListResponse from(final List<DocumentResponse> documents) {
        return DocumentListResponse.builder()
                .documents(documents)
                .build();
    }
}
