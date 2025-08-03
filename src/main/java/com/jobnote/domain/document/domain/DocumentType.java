package com.jobnote.domain.document.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentType {
    RESUME("이력서"),
    COVER_LETTER("자기소개서"),
    PORTFOLIO("포트폴리오"),
    ;

    private final String description;
}
