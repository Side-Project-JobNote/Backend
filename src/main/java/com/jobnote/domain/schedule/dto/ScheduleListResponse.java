package com.jobnote.domain.schedule.dto;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record ScheduleListResponse(
        List<ScheduleResponse> schedules
) {
    public static ScheduleListResponse from(final List<ScheduleResponse> schedules) {
        return ScheduleListResponse.builder()
                .schedules(schedules)
                .build();
    }
}
