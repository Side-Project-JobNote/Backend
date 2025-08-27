package com.jobnote.domain.applicationform.dto;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.domain.ApplicationFormStatus;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ApplicationFormSimpleResponse(
        Long id,
        String companyName,
        String companyAddress,
        ApplicationFormStatus status
) {
    public static ApplicationFormSimpleResponse from(final ApplicationForm applicationForm) {
        return ApplicationFormSimpleResponse.builder()
                .id(applicationForm.getId())
                .companyName(applicationForm.getCompanyName())
                .companyAddress(applicationForm.getCompanyAddress())
                .status(applicationForm.getStatus())
                .build();
    }
}
