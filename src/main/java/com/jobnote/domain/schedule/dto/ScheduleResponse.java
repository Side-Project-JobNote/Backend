package com.jobnote.domain.schedule.dto;

import com.jobnote.domain.schedule.domain.Schedule;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(access = AccessLevel.PRIVATE)
public record ScheduleResponse(
        Long id,
        String title,
        String memo,
        LocalDateTime dateTime
) {
    public static ScheduleResponse from(final Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .memo(schedule.getMemo())
                .dateTime(schedule.getDateTime())
                .build();
    }
}
