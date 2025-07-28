package com.jobnote.domain.applicationform.dto;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.domain.ApplicationFormStatus;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
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
    public static ApplicationFormResponse from(final ApplicationForm form) {
        return ApplicationFormResponse.builder()
                .id(form.getId())
                .companyName(form.getCompanyName())
                .companyTel(form.getCompanyTel())
                .companyAddress(form.getCompanyAddress())
                .companyUrl(form.getCompanyUrl())
                .companyEmail(form.getCompanyEmail())
                .companyScale(form.getCompanyScale())
                .position(form.getPosition())
                .memo(form.getMemo())
                .status(form.getStatus())
                .build();
    }
}
