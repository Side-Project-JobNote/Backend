package com.jobnote.infra.s3.dto;

import java.net.URL;

public record PresignedFileResponse(
        String fileName,
        URL presignedUrl,
        String fileKey
) {
}
