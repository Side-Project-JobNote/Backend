package com.jobnote.domain.document.dto;

import com.jobnote.domain.document.domain.DocumentVersion;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record DocumentVersionResponse(
    Long id,
    int version,
    String fileName,
    Long fileSize,
    String fileUrl
) {
    public static DocumentVersionResponse from(final DocumentVersion documentVersion) {
        return DocumentVersionResponse.builder()
                .id(documentVersion.getId())
                .version(documentVersion.getVersion())
                .fileName(documentVersion.getFileName())
                .fileSize(documentVersion.getFileSize())
                .fileUrl(documentVersion.getFileUrl())
                .build();
    }
}
