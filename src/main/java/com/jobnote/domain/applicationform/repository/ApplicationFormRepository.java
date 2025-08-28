package com.jobnote.domain.applicationform.repository;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Long> {
    Page<ApplicationForm> findAllByUserId(final Long id, final Pageable pageable);
}
