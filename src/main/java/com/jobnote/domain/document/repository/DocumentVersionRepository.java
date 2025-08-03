package com.jobnote.domain.document.repository;

import com.jobnote.domain.document.domain.DocumentVersion;
import com.jobnote.domain.document.dto.DocumentVersionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    /* 해당 문서의 모든 버전 조회 */
    List<DocumentVersion> findAllByDocumentId(final Long documentId);

    @Query("select MAX(v.version) from DocumentVersion v where v.document.id = :documentId")
    Optional<Integer> findMaxVersionByDocumentId(final Long documentId);

    @Query("select v from DocumentVersion v join fetch v.document d join d.user u where u.id = :userId and d.id = :documentId order by v.version desc")
    List<DocumentVersionResponse> findAllByUserIdAndDocumentId(final Long userId, final Long documentId);
}
