package com.jobnote.domain.document.repository;

import com.jobnote.domain.document.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Page<Document> findAllByUserId(final Long userId, final Pageable pageable);
}
