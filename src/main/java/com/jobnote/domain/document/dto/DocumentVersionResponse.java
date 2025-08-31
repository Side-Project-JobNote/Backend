package com.jobnote.domain.document.dto;

import com.jobnote.domain.document.domain.DocumentVersion;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record DocumentVersionResponse(
    Long id,
    Long version,
    String title,
    String fileName,
    String fileUrl,
    Long fileSize
) {
    public static DocumentVersionResponse of(final DocumentVersion documentVersion, final String fileUrl) {
        return DocumentVersionResponse.builder()
                .id(documentVersion.getId())
                .version(documentVersion.getVersion())
                .title(documentVersion.getTitle())
                .fileName(documentVersion.getOriginFileName())
                .fileUrl(fileUrl)
                .fileSize(documentVersion.getFileSize())
                .build();
    }
}
