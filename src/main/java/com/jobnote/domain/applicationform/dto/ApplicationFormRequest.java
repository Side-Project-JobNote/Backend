package com.jobnote.domain.applicationform.dto;

import com.jobnote.domain.applicationform.domain.ApplicationFormStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationFormRequest(

        @NotBlank(message = "회사명은 비어있을 수 없습니다.")
        String companyName,

        String companyTel,

        String companyAddress,

        String companyUrl,

        @Email(message = "형식에 맞는 이메일을 입력해주세요.")
        String companyEmail,

        String companyScale,

        String position,

        String memo,

        @NotNull(message = "지원 상태는 비어있을 수 없습니다.")
        ApplicationFormStatus status
) {
}
