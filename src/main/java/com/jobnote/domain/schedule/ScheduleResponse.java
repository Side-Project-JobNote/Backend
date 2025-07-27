package com.jobnote.domain.schedule;

import java.time.LocalDateTime;

public record ScheduleResponse(
        Long id,
        String title,
        String memo,
        LocalDateTime dateTime
) {
}
