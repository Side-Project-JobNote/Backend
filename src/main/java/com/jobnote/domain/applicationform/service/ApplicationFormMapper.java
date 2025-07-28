package com.jobnote.domain.applicationform.service;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.dto.ApplicationFormRequest;
import com.jobnote.domain.applicationform.dto.ApplicationFormResponse;
import com.jobnote.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationFormMapper {
    public static ApplicationForm toApplicationForm(final User user, final ApplicationFormRequest request) {
        return ApplicationForm.builder()
                .user(user)
                .companyName(request.companyName())
                .companyTel(request.companyTel())
                .companyAddress(request.companyAddress())
                .companyUrl(request.companyUrl())
                .companyEmail(request.companyEmail())
                .companyScale(request.companyScale())
                .position(request.position())
                .memo(request.memo())
                .status(request.status())
                .build();
    }

    public static ApplicationFormResponse fromApplicationForm(final ApplicationForm form) {
        return new ApplicationFormResponse(
                form.getId(),
                form.getCompanyName(),
                form.getCompanyTel(),
                form.getCompanyAddress(),
                form.getCompanyUrl(),
                form.getCompanyEmail(),
                form.getCompanyScale(),
                form.getPosition(),
                form.getMemo(),
                form.getStatus()
        );
    }
}
