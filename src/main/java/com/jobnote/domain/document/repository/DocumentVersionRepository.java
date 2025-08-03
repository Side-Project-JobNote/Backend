package com.jobnote.domain.document.repository;

import com.jobnote.domain.document.domain.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    /* 해당 문서의 모든 버전 조회 */
    List<DocumentVersion> findAllByDocumentId(final Long documentId);

    @Query("SELECT MAX(v.version) FROM DocumentVersion v WHERE v.document.id = :documentId")
    Optional<Integer> findMaxVersionByDocumentId(Long documentId);
}
