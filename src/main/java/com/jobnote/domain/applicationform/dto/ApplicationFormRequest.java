package com.jobnote.domain.applicationform.dto;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.domain.ApplicationFormStatus;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.domain.user.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

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
        ApplicationFormStatus status,

        @Valid
        List<ScheduleRequest> schedules
) {
    public ApplicationForm toEntity(final User user) {
        return ApplicationForm.builder()
                .user(user)
                .companyName(companyName)
                .companyTel(companyTel)
                .companyAddress(companyAddress)
                .companyUrl(companyUrl)
                .companyEmail(companyEmail)
                .companyScale(companyScale)
                .position(position)
                .memo(memo)
                .status(status)
                .build();
    }

    @Override
    public List<ScheduleRequest> schedules() {
        return schedules != null ? schedules : Collections.emptyList();
    }
}
