package com.jobnote.domain.applicationform.repository;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Long> {
    List<ApplicationForm> findAllByUserId(Long id);
}
