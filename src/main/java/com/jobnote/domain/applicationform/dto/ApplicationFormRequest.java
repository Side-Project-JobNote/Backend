package com.jobnote.domain.applicationform.dto;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.domain.ApplicationFormStatus;
import com.jobnote.domain.applicationformdocument.dto.ApplicationFormDocumentRequest;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.domain.user.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ApplicationFormRequest(
        @NotBlank(message = "회사명은 비어있을 수 없습니다.")
        String companyName,

        String companyAddress,

        String companyUrl,

        String companyScale,

        String position,

        @NotNull(message = "지원 상태는 비어있을 수 없습니다.")
        ApplicationFormStatus status,

        @Valid
        List<ScheduleRequest> schedules,

        @Valid
        List<ApplicationFormDocumentRequest> documents
) {
    public ApplicationForm toEntity(final User user) {
        return ApplicationForm.builder()
                .user(user)
                .companyName(companyName)
                .companyAddress(companyAddress)
                .companyUrl(companyUrl)
                .companyScale(companyScale)
                .position(position)
                .status(status)
                .build();
    }

    /* null-safe */
    @Override
    public List<ScheduleRequest> schedules() {
        return schedules != null ? schedules : List.of();
    }

    @Override
    public List<ApplicationFormDocumentRequest> documents() {
        return documents != null ? documents : List.of();
    }
}
