package com.jobnote.domain.document.domain;

import com.jobnote.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "document_version")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentVersion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_version_id")
    private Long id;

    private Long version;

    private String title;

    private String originFileName;

    private String fileKey;

    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Builder
    public DocumentVersion(
            final Long version,
            final String title,
            final String originFileName,
            final String fileKey,
            final Long fileSize,
            final Document document
    ) {
        this.version = version;
        this.title = title;
        this.originFileName = originFileName;
        this.fileKey = fileKey;
        this.fileSize = fileSize;
        this.document = document;
    }
}
