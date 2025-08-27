package com.jobnote.domain.applicationformdocument.domain;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.common.BaseTimeEntity;
import com.jobnote.domain.document.domain.Document;
import com.jobnote.global.exception.JobNoteException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.jobnote.global.common.ResponseCode.FORBIDDEN;

@Entity
@Getter
@Table(name = "application_form_document")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationFormDocument extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_form_document_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_form_id")
    private ApplicationForm applicationForm;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @Builder
    public ApplicationFormDocument(
            final ApplicationForm applicationForm,
            final Document document
    ) {
        this.applicationForm = applicationForm;
        this.document = document;
    }

    public void validateOwner(final Long userId) {
        if (!this.applicationForm.getUser().getId().equals(userId) &&
                !this.document.getUser().getId().equals(userId)) {
            throw new JobNoteException(FORBIDDEN);
        }
    }
}
