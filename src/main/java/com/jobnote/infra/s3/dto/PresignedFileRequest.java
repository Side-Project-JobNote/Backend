package com.jobnote.infra.s3.dto;

import jakarta.validation.constraints.*;

public record PresignedFileRequest(
        @NotBlank(message = "파일 이름은 비어있을 수 없습니다.")
        @Pattern(regexp = "^[^/:*?\"<>|]+$",
                message = "파일 이름에는 / : * ? \" < > | 문자를 사용할 수 없습니다.")
        @Size(max = 100, message = "파일 이름은 100자 이하여야 합니다.")
        String fileName,

        @NotBlank(message = "파일 타입은 비어있을 수 없습니다.")
        String contentType,

        @NotNull(message = "파일 크기는 null일 수 없습니다.")
        @Positive(message = "파일 크기는 양수여야 합니다.")
        Long fileSize
) {
}
