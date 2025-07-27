package com.jobnote.domain.schedule.service;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.schedule.domain.Schedule;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleMapper {
    public static Schedule toSchedule(final ApplicationForm form, final ScheduleRequest request) {
        return Schedule.builder()
                .applicationForm(form)
                .title(request.title())
                .memo(request.memo())
                .dateTime(request.dateTime())
                .build();
    }

    public static ScheduleResponse fromSchedule(final Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getMemo(),
                schedule.getDateTime()
        );
    }
}
