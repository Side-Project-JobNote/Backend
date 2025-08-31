package com.jobnote.domain.document.dto;

import com.jobnote.domain.applicationform.dto.ApplicationFormSimpleResponse;
import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.domain.DocumentType;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record DocumentResponse(
        Long id,
        DocumentType type,
        String title,
        LocalDate lastModifiedDate,
        Long latestVersion,
        List<ApplicationFormSimpleResponse> applicationForms
) {
    public static DocumentResponse of(final Document document, final Long latestVersion, final List<ApplicationFormSimpleResponse> applicationForms) {
        return DocumentResponse.builder()
                .id(document.getId())
                .type(document.getType())
                .title(document.getTitle())
                .lastModifiedDate(document.getModifiedDate().toLocalDate())
                .latestVersion(latestVersion)
                .applicationForms(applicationForms)
                .build();
    }
}
