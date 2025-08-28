package com.jobnote.domain.document.dto;

import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.domain.DocumentType;
import com.jobnote.domain.user.domain.User;
import jakarta.validation.constraints.*;

public record DocumentRequest(
        @NotBlank(message = "문서 제목은 비어있을 수 없습니다.")
        @Size(max = 100, message = "문서 제목은 100자 이하여야 합니다.")
        String title,

        @NotBlank(message = "파일 이름은 비어있을 수 없습니다.")
        @Pattern(regexp = "^[^/:*?\"<>|]+$",
                message = "파일 이름에는 / : * ? \" < > | 문자를 사용할 수 없습니다.")
        @Size(max = 100, message = "파일 이름은 100자 이하여야 합니다.")
        String fileName,

        @NotBlank(message = "파일 key는 비어있을 수 없습니다.")
        @Pattern(regexp = "^\\d+/[^/:*?\"<>|]+$", // userId/uuid_fileName.type 형식 검사
                 message = "올바른 파일 key를 입력해주세요.")
        String fileKey,

        @NotNull(message = "파일 종류는 비어있을 수 없습니다.")
        DocumentType fileType,

        @NotNull(message = "파일 크기는 null일 수 없습니다.")
        @Positive(message = "파일 크기는 양수여야 합니다.")
        Long fileSize
) {
    public Document toEntity(final User user) {
        return Document.builder()
                .user(user)
                .documentType(fileType)
                .title(title)
                .build();
    }
}
