package com.jobnote.domain.code.service;

import com.jobnote.domain.code.domain.TempCode;
import com.jobnote.domain.code.repository.TempCodeRepository;
import com.jobnote.domain.common.Time;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.jobnote.global.common.ResponseCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TempCodeService {

    private final TempCodeRepository tempCodeRepository;
    private final Time time;

    @Transactional
    public String create(final long userId) {
        final String code = UUID.randomUUID().toString();
        tempCodeRepository.save(TempCode.of(code, userId, time.now().plusMinutes(1)));

        return code;
    }

    @Transactional
    public long validate(final String code) {
        final TempCode tempCode = tempCodeRepository.findById(code)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_TEMP_CODE));

        tempCode.validateExpiration(time.now());

        tempCodeRepository.delete(tempCode);

        return tempCode.getUserId();
    }
}
