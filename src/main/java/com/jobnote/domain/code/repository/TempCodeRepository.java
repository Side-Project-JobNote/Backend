package com.jobnote.domain.code.repository;

import com.jobnote.domain.code.domain.TempCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempCodeRepository extends JpaRepository<TempCode, String> {
}
