package com.jobnote.s3.dto;

import java.net.URL;

public record PresignedFileResponse(
        String fileName,
        URL presignedUrl,
        String fileUrl
) {
}
