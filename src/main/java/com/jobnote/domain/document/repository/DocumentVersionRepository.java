package com.jobnote.domain.document.repository;

import com.jobnote.domain.document.domain.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    /* 해당 문서의 모든 버전 조회 */
    List<DocumentVersion> findAllByDocumentId(final Long documentId);
}
