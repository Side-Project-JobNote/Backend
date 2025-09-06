package com.jobnote.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;

public record TokenIssueRequest(

        @NotEmpty(message = "코드는 비어 있을 수 없습니다.")
        String code
) {
}
