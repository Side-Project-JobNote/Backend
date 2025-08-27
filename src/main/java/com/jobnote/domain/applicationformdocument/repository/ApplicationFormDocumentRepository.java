package com.jobnote.domain.applicationformdocument.repository;

import com.jobnote.domain.applicationformdocument.domain.ApplicationFormDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationFormDocumentRepository extends JpaRepository<ApplicationFormDocument, Long> {
    // 해당 지원서에 속한 문서(지원서-문서 맵핑 엔티티) 목록 반환
    @Query("select afd from ApplicationFormDocument afd join fetch afd.applicationForm af join fetch af.user where af.user.id = :userId and af.id in :formId")
    List<ApplicationFormDocument> findAllByUserIdAndApplicationFormIdIn(final Long userId, final List<Long> formId);

    // 해당 지원서에 속한 문서 연결 해제
    void deleteAllByApplicationFormId(final Long formId);

    // 해당 문서에 속한 지원서 연결 해제
    void deleteAllByDocumentId(final Long documentId);
}
