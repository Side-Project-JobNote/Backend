package com.jobnote.domain.applicationformdocument.service;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.dto.ApplicationFormSimpleResponse;
import com.jobnote.domain.applicationformdocument.domain.ApplicationFormDocument;
import com.jobnote.domain.applicationformdocument.dto.ApplicationFormDocumentRequest;
import com.jobnote.domain.applicationformdocument.repository.ApplicationFormDocumentRepository;
import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.dto.DocumentSimpleResponse;
import com.jobnote.domain.document.repository.DocumentRepository;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_DOCUMENT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationFormDocumentService {

    private final ApplicationFormDocumentRepository applicationFormDocumentRepository;
    private final DocumentRepository documentRepository;

    /* READ */
    // 해당 지원서의 등록된 문서 목록 반환
    public List<ApplicationFormDocument> getAllByApplicationFormId(final Long userId, final Long formId) {
        return applicationFormDocumentRepository.findAllByUserIdAndApplicationFormIdIn(userId, List.of(formId));
    }

    // 해당 문서와 연결된 지원서 목록 반환
    public List<ApplicationFormSimpleResponse> getAllSimpleByDocumentId(final Long userId, final Long documentId) {
        return applicationFormDocumentRepository.findAllByUserIdAndDocumentId(userId, documentId).stream()
                .map(afd -> ApplicationFormSimpleResponse.from(afd.getApplicationForm()))
                .toList();
    }

    // 해당 지원서와 연결된 문서 목록 반환
    public List<DocumentSimpleResponse> getAllSimpleByApplicationFormId(final Long userId, final Long formId) {
        return applicationFormDocumentRepository.findAllByUserIdAndApplicationFormIdIn(userId, List.of(formId)).stream()
                .map(afd -> DocumentSimpleResponse.from(afd.getDocument()))
                .toList();
    }

    // 각 지원서에 대한 등록된 문서 목록 반환
    public Map<Long, List<DocumentSimpleResponse>> getAllSimpleGroupedByApplicationFormIds(final Long userId, final List<Long> formIds) {
        List<ApplicationFormDocument> list = applicationFormDocumentRepository.findAllByUserIdAndApplicationFormIdIn(userId, formIds);

        return list.stream()
                .collect(Collectors.groupingBy(
                        // 각 문서가 속한 지원서의 ID를 기준으로 그룹핑
                        document -> document.getApplicationForm().getId(),
                        // 그룹핑된 문서들을 DocumentSimpleResponse로 변환하여 List로 수집
                        Collectors.mapping(
                                afd -> DocumentSimpleResponse.from(afd.getDocument()),
                                Collectors.toList()
                        )
                ));
    }

    /* CREATE */
    @Transactional
    public void saveAll(final Long userId, final ApplicationForm form, final List<ApplicationFormDocumentRequest> requests) {
        for (ApplicationFormDocumentRequest request : requests) {
            Document document = getByIdOrThrow(request.id());
            document.validateOwner(userId);

            ApplicationFormDocument entity = request.toEntity(form, document);
            applicationFormDocumentRepository.save(entity);
        }
    }

    /* UPDATE */
    @Transactional
    public void updateAll(final Long userId, final ApplicationForm form, final List<ApplicationFormDocumentRequest> requests) {
        // 기존 연결된 문서 조회
        List<ApplicationFormDocument> existsDocuments = getAllByApplicationFormId(userId, form.getId());
        Set<Long> existsIds =  existsDocuments.stream()
                .map(document -> document.getDocument().getId())
                .collect(Collectors.toSet());

        // 요청 문서 ID 목록
        Set<Long> requestsIds = requests.stream()
                .map(ApplicationFormDocumentRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 삭제 : 기존 문서 중 요청에 없는 문서 삭제
        for (ApplicationFormDocument document : existsDocuments) {
            if (!requestsIds.contains(document.getDocument().getId())) {
                delete(document);
            }
        }

        // 신규 생성 : 새로운 id
        for (ApplicationFormDocumentRequest req : requests) {
            if (!existsIds.contains(req.id())) {
                saveAll(userId, form, List.of(req));
            }
        }
    }

    /* DELETE */
    @Transactional
    public void deleteAllByApplicationFormId(final Long formId) {
        applicationFormDocumentRepository.deleteAllByApplicationFormId(formId);
    }

    @Transactional
    public void deleteAllByDocumentId(final Long documentId) {
        applicationFormDocumentRepository.deleteAllByDocumentId(documentId);
    }

    @Transactional
    public void delete(final ApplicationFormDocument document) {
        applicationFormDocumentRepository.delete(document);
    }

    /* HELPER METHOD */
    private Document getByIdOrThrow(final Long docId) {
        return documentRepository.findById(docId)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_DOCUMENT));
    }
}
