package com.jobnote.domain.schedule.dto;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.schedule.domain.Schedule;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record ScheduleRequest(

        Long id,

        @NotBlank(message = "제목은 비어있을 수 없습니다.")
        String title,

        String memo,

        @NotBlank(message = "날짜는 비어있을 수 없습니다.")
        LocalDateTime dateTime
) {
   public Schedule toEntity(final ApplicationForm form) {
           return Schedule.builder()
                   .applicationForm(form)
                   .title(title)
                   .memo(memo)
                   .dateTime(dateTime)
                   .build();
   }
}
