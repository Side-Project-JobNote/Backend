package com.jobnote.domain.schedule.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record ScheduleRequest(

        @NotBlank(message = "제목은 비어있을 수 없습니다.")
        String title,

        String memo,

        @NotBlank(message = "날짜는 비어있을 수 없습니다.")
        LocalDateTime dateTime
) {
}
