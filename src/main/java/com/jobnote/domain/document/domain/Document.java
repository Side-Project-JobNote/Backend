package com.jobnote.domain.document.domain;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.common.BaseTimeEntity;
import com.jobnote.domain.user.domain.User;
import com.jobnote.global.exception.JobNoteException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.jobnote.global.common.ResponseCode.FORBIDDEN;

@Entity
@Getter
@Table(name = "document")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationForm applicationForm;

    @Enumerated(EnumType.STRING)
    private DocumentType type;

    private String title;

    @Builder
    public Document(
            final User user,
            final ApplicationForm applicationForm,
            final DocumentType documentType,
            final String title
    ) {
        this.user = user;
        this.applicationForm = applicationForm;
        this.type = documentType;
        this.title = title;
    }

    public void validateOwner(final Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new JobNoteException(FORBIDDEN);
        }
    }
}
