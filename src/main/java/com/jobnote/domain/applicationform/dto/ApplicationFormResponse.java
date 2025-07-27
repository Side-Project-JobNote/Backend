package com.jobnote.domain.applicationform.dto;

import com.jobnote.domain.applicationform.domain.ApplicationFormStatus;

public record ApplicationFormResponse(
        Long id,
        String companyName,
        String companyTel,
        String companyAddress,
        String companyUrl,
        String companyEmail,
        String companyScale,
        String position,
        String memo,
        ApplicationFormStatus status
) {
}
