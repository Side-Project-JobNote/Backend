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

    private int version;

    private String fileName;

    private Long fileSize;

    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Builder
    public DocumentVersion(
            final int version,
            final String fileName,
            final Long fileSize,
            final String fileUrl,
            final Document document
    ) {
        this.version = version;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.document = document;
    }
}
