package com.jobnote.domain.applicationform.dto;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.domain.ApplicationFormStatus;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.Collections;
import java.util.List;

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
        ApplicationFormStatus status,
        List<ScheduleResponse> schedules
) {
    public static ApplicationFormResponse from(final ApplicationForm form, final List<ScheduleResponse> schedules) {
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
                .schedules(schedules)
                .build();
    }

    @Override
    public List<ScheduleResponse> schedules() {
        return schedules != null ? schedules : Collections.emptyList();
    }
}
