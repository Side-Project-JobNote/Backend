package com.jobnote.domain.document.repository;

import com.jobnote.domain.document.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findAllByUserId(final Long userId);
}
