package com.jobnote.domain.applicationForm;

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
