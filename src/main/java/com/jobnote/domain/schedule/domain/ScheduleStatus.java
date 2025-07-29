package com.jobnote.domain.schedule.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleStatus {
    PLANNED("예정됨"),
    PROGRESS("준비중"),
    COMPLETED("완료됨"),
    CANCELLED("취소됨"),
    ;

    private final String description;
}
