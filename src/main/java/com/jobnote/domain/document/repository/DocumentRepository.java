package com.jobnote.domain.document.repository;

import com.jobnote.domain.document.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    /* 해당 지원서의 문서 목록 조회 */
    @Query("select d from Document d join fetch d.applicationForm af join fetch af.user where af.user.id = :userId and af.id = :formId")
    List<Document> findAllByUserIdAndApplicationFormId(final Long userId, final Long formId);

    List<Document> findAllByUserId(final Long userId);
}
