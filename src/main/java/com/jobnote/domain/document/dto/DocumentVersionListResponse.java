package com.jobnote.domain.document.dto;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record DocumentVersionListResponse(
        List<DocumentVersionResponse> documentVersions
) {
    public static DocumentVersionListResponse from(List<DocumentVersionResponse> documentVersions) {
        return DocumentVersionListResponse.builder()
                .documentVersions(documentVersions)
                .build();
    }
}
