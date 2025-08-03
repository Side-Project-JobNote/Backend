package com.jobnote.s3.dto;

public record PresignedFileRequest(
    String fileName,
    String fileType,
    Long fileSize
) {
}
