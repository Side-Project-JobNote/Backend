package com.jobnote.domain.applicationformdocument.dto;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationformdocument.domain.ApplicationFormDocument;
import com.jobnote.domain.document.domain.Document;
import jakarta.validation.constraints.NotNull;

public record ApplicationFormDocumentRequest(
        @NotNull(message = "문서 ID는 null일 수 없습니다.")
        Long id
) {
    public ApplicationFormDocument toEntity(final ApplicationForm form, final Document document) {
        return ApplicationFormDocument.builder()
                .applicationForm(form)
                .document(document)
                .build();
    }
}
