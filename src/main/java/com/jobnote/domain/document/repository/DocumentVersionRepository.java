package com.jobnote.domain.document.repository;

import com.jobnote.domain.document.domain.DocumentVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    @Query("select MAX(v.version) from DocumentVersion v where v.document.id = :documentId")
    Optional<Integer> findMaxVersionByDocumentId(final Long documentId);

    @Query("select v from DocumentVersion v join fetch v.document d join d.user u where u.id = :userId and d.id = :documentId order by v.version desc")
    Page<DocumentVersion> findAllByUserIdAndDocumentId(final Long userId, final Long documentId, final Pageable pageable);

    @Query("select COALESCE(SUM(v.fileSize), 0) from DocumentVersion v join v.document d where d.user.id = :userId")
    Long getTotalFileSizeByUserId(final Long userId);

    @Modifying(clearAutomatically = true)
    void deleteAllByDocumentId(final Long documentId);
}
