package com.jobnote.domain.common;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JobNoteTime implements Time {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
