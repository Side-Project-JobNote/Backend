package com.jobnote.domain.document.repository;

import com.jobnote.domain.document.domain.DocumentVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    @Query("select v from DocumentVersion v join fetch v.document d join d.user u where u.id = :userId and d.id = :documentId order by v.version desc")
    Page<DocumentVersion> findAllByUserIdAndDocumentId(final Long userId, final Long documentId, final Pageable pageable);

    /* 해당 문서의 최신 버전 조회 */
    @Query("select MAX(v.version) from DocumentVersion v where v.document.id = :documentId")
    Optional<Integer> findLatestVersionByDocumentId(final Long documentId);

    /* 해당 유저의 문서 총 이용 용량 조회 */
    @Query("select COALESCE(SUM(v.fileSize), 0) from DocumentVersion v join v.document d where d.user.id = :userId")
    Long getTotalFileSizeByUserId(final Long userId);

    /* 해당 문서에 등록된 문서 버전들 모두 삭제 */
    @Modifying(clearAutomatically = true)
    void deleteAllByDocumentId(final Long documentId);
}
