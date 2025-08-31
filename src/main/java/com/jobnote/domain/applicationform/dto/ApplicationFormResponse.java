package com.jobnote.domain.applicationform.dto;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.domain.ApplicationFormStatus;
import com.jobnote.domain.document.dto.DocumentSimpleResponse;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record ApplicationFormResponse(
        Long id,
        String companyName,
        String companyAddress,
        String companyUrl,
        String companyScale,
        String position,
        ApplicationFormStatus status,
        List<ScheduleResponse> schedules,
        List<DocumentSimpleResponse> documents
) {
    public static ApplicationFormResponse from(final ApplicationForm form, final List<ScheduleResponse> schedules, final List<DocumentSimpleResponse> documents) {
        return ApplicationFormResponse.builder()
                .id(form.getId())
                .companyName(form.getCompanyName())
                .companyAddress(form.getCompanyAddress())
                .companyUrl(form.getCompanyUrl())
                .companyScale(form.getCompanyScale())
                .position(form.getPosition())
                .status(form.getStatus())
                .schedules(schedules)
                .documents(documents)
                .build();
    }

    /* null-safe */
    @Override
    public List<ScheduleResponse> schedules() {
        return schedules != null ? schedules : List.of();
    }

    @Override
    public List<DocumentSimpleResponse> documents() {
        return documents != null ? documents : List.of();
    }
}
