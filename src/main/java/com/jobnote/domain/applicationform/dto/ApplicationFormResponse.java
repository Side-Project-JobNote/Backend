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
        String companyTel,
        String companyAddress,
        String companyUrl,
        String companyEmail,
        String companyScale,
        String position,
        String memo,
        ApplicationFormStatus status,
        List<ScheduleResponse> schedules,
        List<DocumentSimpleResponse> documents
) {
    public static ApplicationFormResponse from(final ApplicationForm form, final List<ScheduleResponse> schedules, final List<DocumentSimpleResponse> documents) {
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
