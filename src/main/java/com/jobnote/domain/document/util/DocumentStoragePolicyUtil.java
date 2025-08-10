package com.jobnote.domain.document.util;

import com.jobnote.domain.document.repository.DocumentVersionRepository;
import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
@RequiredArgsConstructor
public class DocumentStoragePolicyUtil {

    private final DocumentVersionRepository documentVersionRepository;

    @Value("${file.quota.per-user}")
    private DataSize maxUploadSize;

    public void validateTotalStorageQuota(final Long userId, final Long fileSize) {
        long currentSize = documentVersionRepository.getTotalFileSizeByUserId(userId);

        if (currentSize + fileSize > maxUploadSize.toBytes()) {
            throw new JobNoteException(ResponseCode.UPLOAD_SIZE_LIMIT_EXCEEDED);
        }
    }
}
