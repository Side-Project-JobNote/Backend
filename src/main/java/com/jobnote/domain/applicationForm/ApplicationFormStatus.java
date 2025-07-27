package com.jobnote.domain.applicationForm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationFormStatus {
    PLANNED("지원 예정"),
    APPLIED("지원 완료"),
    DOCUMENT_PASSED("서류 합격"),
    FINAL_PASSED("최종 합격"),
    REJECTED("불합격"),
    ;

    private final String description;
}
