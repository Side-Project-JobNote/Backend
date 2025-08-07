package com.jobnote.s3.service;

import com.jobnote.domain.document.util.DocumentStoragePolicyUtil;
import com.jobnote.s3.dto.PresignedFileRequest;
import com.jobnote.s3.dto.PresignedFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final DocumentStoragePolicyUtil documentStoragePolicyUtil;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    public PresignedFileResponse generatePresignedFile(final Long userId, final PresignedFileRequest request) {
        // 총 업로드 허용 용량을 초과하지 않는지 검증
        documentStoragePolicyUtil.validateTotalStorageQuota(userId, request.fileSize());

        // Url 발급
        String key = createUniqueFileName(userId, request.fileName());
        return new PresignedFileResponse(request.fileName(), generatePresignedUrl(key, request.contentType()), key);
    }

    public String generateFileUrl(final String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }

    /* HELPER METHOD */
    private URL generatePresignedUrl(final String key, final String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        return presigned.url();
    }

    private String createUniqueFileName(final Long userId, final String originalFileName) {
        return String.format("%d/%s_%s", userId, UUID.randomUUID(), originalFileName);
    }
}
