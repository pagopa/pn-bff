package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.notifications.*;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

import static it.pagopa.pn.bff.exceptions.PnBffExceptionCodes.ERROR_CODE_BFF_DOCUMENTIDNOTFOUND;
import static it.pagopa.pn.bff.exceptions.PnBffExceptionCodes.ERROR_CODE_BFF_LEGALFACTTYPENOTFOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationsPAService {

    private final PnDeliveryClientPAImpl pnDeliveryClient;
    private final PnDeliveryPushClientImpl pnDeliveryPushClient;

    /**
     * Search the notifications sent by a Public Administration
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param iun               Notification IUN
     * @param senderId          Sender Identifier
     * @param status            Notification Status
     * @param subjectRegExp     Regular Expression for the subject
     * @param startDate         Start Date
     * @param endDate           End Date
     * @param size              Page number
     * @param nextPagesKey      Page size
     * @return the list of notifications sent by a Public Administration
     */
    public Mono<BffNotificationsResponse> searchSentNotifications(String xPagopaPnUid,
                                                                  CxTypeAuthFleet xPagopaPnCxType,
                                                                  String xPagopaPnCxId,
                                                                  List<String> xPagopaPnCxGroups,
                                                                  String iun,
                                                                  String senderId,
                                                                  NotificationStatus status,
                                                                  String subjectRegExp,
                                                                  OffsetDateTime startDate,
                                                                  OffsetDateTime endDate,
                                                                  Integer size,
                                                                  String nextPagesKey) {
        log.info("searchSentNotifications");

        Mono<NotificationSearchResponse> notifications = pnDeliveryClient.searchSentNotifications(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryWebPACXType(xPagopaPnCxType),
                xPagopaPnCxId,
                startDate,
                endDate,
                xPagopaPnCxGroups,
                senderId,
                NotificationStatusMapper.notificationStatusMapper.convertDeliveryWebPANotificationStatus(status),
                subjectRegExp,
                iun,
                size,
                nextPagesKey
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return notifications.map(NotificationsSentMapper.modelMapper::toBffNotificationsResponse);
    }

    /**
     * Get the detail of a notification. This is for a Public Administration user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the detail of the notification with a specific IUN
     */
    public Mono<BffFullNotificationV1> getSentNotificationDetail(String xPagopaPnUid,
                                                                 CxTypeAuthFleet xPagopaPnCxType,
                                                                 String xPagopaPnCxId, String iun,
                                                                 List<String> xPagopaPnCxGroups
    ) {
        log.info("Get notification detail for iun {}", iun);
        Mono<FullSentNotificationV23> notificationDetail = pnDeliveryClient.getSentNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return notificationDetail.map(NotificationDetailMapper.modelMapper::mapSentNotificationDetail);
    }

    /**
     * Download the document linked to a notification
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param documentId        the document id (safestorage key if aar or legalfact, the index in the array if attachment)
     * @param documentType      the document type (aar, attachment or legal fact)
     * @param legalFactCategory the legal fact category (required only if the documentType is legal fact)
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the requested document
     */
    public Mono<BffDocumentDownloadMetadataResponse> getSentNotificationDocument(String xPagopaPnUid,
                                                                                 CxTypeAuthFleet xPagopaPnCxType,
                                                                                 String xPagopaPnCxId, String iun,
                                                                                 DocumentId documentId,
                                                                                 BffDocumentType documentType,
                                                                                 LegalFactCategory legalFactCategory,
                                                                                 List<String> xPagopaPnCxGroups
    ) {
        log.info("Get notification {} for iun {}", documentType, iun);

        if (documentType == BffDocumentType.ATTACHMENT) {
            if (documentId.getAttachmentIdx() == null) {
                return Mono.error(new PnBffException(
                        "Document id not found",
                        "The document id is missed",
                        HttpStatus.BAD_REQUEST.value(),
                        ERROR_CODE_BFF_DOCUMENTIDNOTFOUND
                ));
            }
            Mono<NotificationAttachmentDownloadMetadataResponse> attachment = pnDeliveryClient.getSentNotificationDocument(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    iun,
                    documentId.getAttachmentIdx(),
                    xPagopaPnCxGroups
            ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
            return attachment.map(NotificationDownloadDocumentMapper.modelMapper::mapSentAttachmentDownloadResponse);
        } else if (documentType == BffDocumentType.AAR) {
            if (documentId.getAarId() == null) {
                return Mono.error(new PnBffException(
                        "Document id not found",
                        "The document id is missed",
                        HttpStatus.BAD_REQUEST.value(),
                        ERROR_CODE_BFF_DOCUMENTIDNOTFOUND
                ));
            }
            Mono<DocumentDownloadMetadataResponse> document = pnDeliveryPushClient.getDocumentsWeb(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertDeliveryPushCXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    iun,
                    DocumentCategory.AAR,
                    documentId.getAarId(),
                    xPagopaPnCxGroups,
                    null
            ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
            return document.map(NotificationDownloadDocumentMapper.modelMapper::mapDocumentDownloadResponse);
        } else {
            if (documentId.getLegalFactId() == null) {
                return Mono.error(new PnBffException(
                        "Document id not found",
                        "The document id is missed",
                        HttpStatus.BAD_REQUEST.value(),
                        ERROR_CODE_BFF_DOCUMENTIDNOTFOUND
                ));
            }
            if (legalFactCategory == null) {
                return Mono.error(new PnBffException(
                        "Legal fact category not found",
                        "The legal fact category is missed",
                        HttpStatus.BAD_REQUEST.value(),
                        ERROR_CODE_BFF_LEGALFACTTYPENOTFOUND
                ));
            }
            // others legal fact case
            Mono<LegalFactDownloadMetadataResponse> legalFact = pnDeliveryPushClient.getLegalFact(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertDeliveryPushCXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    iun,
                    NotificationParamsMapper.modelMapper.mapLegalFactCategory(legalFactCategory),
                    documentId.getLegalFactId(),
                    xPagopaPnCxGroups,
                    null
            ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
            return legalFact.map(NotificationDownloadDocumentMapper.modelMapper::mapLegalFactDownloadResponse);
        }

    }
}