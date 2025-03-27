package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV27;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.RequestStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.notifications.*;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

import static it.pagopa.pn.bff.exceptions.PnBffExceptionCodes.ERROR_CODE_BFF_DOCUMENTIDNOTFOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationsPAService {

    private final PnDeliveryClientPAImpl pnDeliveryClient;
    private final PnDeliveryPushClientImpl pnDeliveryPushClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

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
                                                                  NotificationStatusV26 status,
                                                                  String subjectRegExp,
                                                                  OffsetDateTime startDate,
                                                                  OffsetDateTime endDate,
                                                                  Integer size,
                                                                  String nextPagesKey) {
        log.info("Search notifications - senderId: {} - type: {} - groups: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

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
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

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
        log.info("Get notification detail - senderId: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        Mono<FullSentNotificationV27> notificationDetail = pnDeliveryClient.getSentNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return notificationDetail.map(NotificationSentDetailMapper.modelMapper::mapSentNotificationDetail);
    }

    /**
     * Download the document linked to a notification. This is for a Public Administration user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param documentType      the document type (aar, attachment or legal fact)
     * @param documentIdx       the document index if attachment
     * @param documentId        the document id if aar or legal fact
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the requested document
     */
    public Mono<BffDocumentDownloadMetadataResponse> getSentNotificationDocument(String xPagopaPnUid,
                                                                                 CxTypeAuthFleet xPagopaPnCxType,
                                                                                 String xPagopaPnCxId, String iun,
                                                                                 BffDocumentType documentType,
                                                                                 Integer documentIdx,
                                                                                 String documentId,
                                                                                 List<String> xPagopaPnCxGroups
    ) {
        log.info("Get notification document - senderId: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        if (documentType == BffDocumentType.ATTACHMENT) {
            if (documentIdx == null) { // attachment case
                log.error("Attachment idx not found");
                return Mono.error(new PnBffException(
                        "Attachment idx not found",
                        "The attachment idx is missed",
                        HttpStatus.BAD_REQUEST.value(),
                        ERROR_CODE_BFF_DOCUMENTIDNOTFOUND
                ));
            }
            Mono<NotificationAttachmentDownloadMetadataResponse> attachment = pnDeliveryClient.getSentNotificationDocument(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    iun,
                    documentIdx,
                    xPagopaPnCxGroups
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
            return attachment.map(NotificationDownloadDocumentMapper.modelMapper::mapSentAttachmentDownloadResponse);
        } else if (documentType == BffDocumentType.AAR) { // AAR case
            if (documentId == null) {
                log.error("AAR id not found");
                return Mono.error(new PnBffException(
                        "AAR id not found",
                        "The AAR id is missed",
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
                    documentId,
                    xPagopaPnCxGroups,
                    null
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
            return document.map(NotificationDownloadDocumentMapper.modelMapper::mapDocumentDownloadResponse);
        } else { // legal fact case
            if (documentId == null) {
                log.error("Legal fact id not found");
                return Mono.error(new PnBffException(
                        "Legal fact id not found",
                        "The legal fact id is missed",
                        HttpStatus.BAD_REQUEST.value(),
                        ERROR_CODE_BFF_DOCUMENTIDNOTFOUND
                ));
            }
            Mono<LegalFactDownloadMetadataResponse> legalFact = pnDeliveryPushClient.getLegalFact(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertDeliveryPushCXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    iun,
                    documentId,
                    xPagopaPnCxGroups,
                    null
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

            return legalFact.map(NotificationDownloadDocumentMapper.modelMapper::mapLegalFactDownloadResponse);
        }
    }

    /**
     * Get the payment for a notification. This is for a Public Administration user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param recipientIdx      Index of the recipient for which download the payment
     * @param attachmentName    Type of the payment (PAGOPA or F24)
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param attachmentIdx     Index of the payment
     * @return the payment for the notification with a specific IUN
     */
    public Mono<BffDocumentDownloadMetadataResponse> getSentNotificationPayment(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                String xPagopaPnCxId, String iun, Integer recipientIdx,
                                                                                String attachmentName, List<String> xPagopaPnCxGroups,
                                                                                Integer attachmentIdx
    ) {
        log.info("Get notification payment - senderId: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        Mono<NotificationAttachmentDownloadMetadataResponse> notificationDetail = pnDeliveryClient.getSentNotificationPayment(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iun,
                recipientIdx,
                attachmentName,
                xPagopaPnCxGroups,
                attachmentIdx
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return notificationDetail.map(NotificationDownloadDocumentMapper.modelMapper::mapSentAttachmentDownloadResponse);
    }

    /**
     * Cancel a notification
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the status of the notification cancellation
     */
    public Mono<BffRequestStatus> notificationCancellation(String xPagopaPnUid,
                                                           CxTypeAuthFleet xPagopaPnCxType,
                                                           String xPagopaPnCxId,
                                                           String iun,
                                                           List<String> xPagopaPnCxGroups) {
        log.info("Cancel notification - senderId: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        Mono<RequestStatus> bffRequestStatus = pnDeliveryPushClient.notificationCancellation(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertDeliveryPushCXType(xPagopaPnCxType), xPagopaPnCxId, iun, xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return bffRequestStatus.map(NotificationCancellationMapper.modelMapper::mapNotificationCancellation);
    }

    /**
     * Create new notification
     *
     * @param xPagopaPnUid           User Identifier
     * @param xPagopaPnCxType        Public Administration Type
     * @param xPagopaPnCxId          Public Administration id
     * @param newNotificationRequest The request that contains the notification to create
     * @param xPagopaPnCxGroups      Public Administration Group id List
     * @return the request of the newly created notification
     */
    public Mono<BffNewNotificationResponse> newSentNotification(String xPagopaPnUid,
                                                                CxTypeAuthFleet xPagopaPnCxType,
                                                                String xPagopaPnCxId,
                                                                Mono<BffNewNotificationRequest> newNotificationRequest,
                                                                List<String> xPagopaPnCxGroups) {
        log.info("Create notification - senderId: {} - type: {} - groups: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        return newNotificationRequest.flatMap(request ->
                pnDeliveryClient.newSentNotification(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(xPagopaPnCxType), xPagopaPnCxId, NewSentNotificationMapper.modelMapper.mapRequest(request), xPagopaPnCxGroups)
                        .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
                        .map(NewSentNotificationMapper.modelMapper::mapResponse)
        );
    }

    /**
     * Upload one or more documents
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param bffPreLoadRequest Request to get the pre-signed urls
     * @return the ids of the uploaded documents
     */
    public Flux<BffPreLoadResponse> preSignedUpload(String xPagopaPnUid,
                                                    CxTypeAuthFleet xPagopaPnCxType,
                                                    String xPagopaPnCxId,
                                                    Flux<BffPreLoadRequest> bffPreLoadRequest) {
        log.info("Request pre-signed url to upload document - senderId: {} - type: {}",
                xPagopaPnCxId, xPagopaPnCxType);

        return bffPreLoadRequest.collectList().flatMapMany(request ->
                pnDeliveryClient.preSignedUpload(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(xPagopaPnCxType), xPagopaPnCxId, NotificationSentPreloadDocumentsMapper.modelMapper.mapRequest(request))
                        .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
                        .map(NotificationSentPreloadDocumentsMapper.modelMapper::mapResponse)
        );
    }
}