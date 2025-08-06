package com.jobnote.domain.applicationform.dto;

import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record ApplicationFormListResponse(
        List<ApplicationFormResponse> applicationForms
) {
    public static ApplicationFormListResponse from(final List<ApplicationFormResponse> applicationForms) {
        return ApplicationFormListResponse.builder()
                .applicationForms(applicationForms)
                .build();
    }
}