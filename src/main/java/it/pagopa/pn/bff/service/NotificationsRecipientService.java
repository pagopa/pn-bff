package it.pagopa.pn.bff.service;


import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV25;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.notifications.*;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import it.pagopa.pn.bff.pnclient.emd.PnEmdClientImpl;
import it.pagopa.pn.bff.utils.CommonUtility;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.bff.exceptions.PnBffExceptionCodes.ERROR_CODE_BFF_DOCUMENTIDNOTFOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationsRecipientService {

    private final PnDeliveryClientRecipientImpl pnDeliveryClient;
    private final PnDeliveryPushClientImpl pnDeliveryPushClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;
    private final PnEmdClientImpl pnEmdClient;

    /**
     * Search received notifications for a recipient user.
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxId     Receiver id
     * @param iunMatch          Notification IUN
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param mandateId         mandate id. It is required if the user, that is requesting the notification, is a mandate
     * @param senderId          Sender id
     * @param status            Notification status
     * @param startDate         Start date
     * @param endDate           End date
     * @param subjectRegExp     Regular expression for the subject
     * @param size              Number of notifications to retrieve
     * @param nextPagesKey      Key to retrieve the next page
     * @return the list of notifications
     */
    public Mono<BffNotificationsResponse> searchReceivedNotifications(String xPagopaPnUid,
                                                                      CxTypeAuthFleet xPagopaPnCxType,
                                                                      String xPagopaPnCxId,
                                                                      String iunMatch,
                                                                      List<String> xPagopaPnCxGroups,
                                                                      String mandateId,
                                                                      String senderId,
                                                                      NotificationStatusV26 status,
                                                                      OffsetDateTime startDate,
                                                                      OffsetDateTime endDate,
                                                                      String subjectRegExp,
                                                                      Integer size,
                                                                      String nextPagesKey) {
        log.info("Search notifications - senderId: {} - type: {} - groups: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        Mono<NotificationSearchResponse> notifications = pnDeliveryClient.searchReceivedNotifications(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iunMatch,
                xPagopaPnCxGroups,
                mandateId,
                senderId,
                NotificationStatusMapper.notificationStatusMapper.convertDeliveryRecipientNotificationStatus(status),
                startDate,
                endDate,
                subjectRegExp,
                size,
                nextPagesKey
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return notifications.map(NotificationsReceivedMapper.modelMapper::toBffNotificationsResponse);
    }

    /**
     * Search received delegated notifications for a recipient user.
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxId     Receiver id
     * @param iunMatch          Notification IUN
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param senderId          Sender id
     * @param recipientId       Recipient id
     * @param group             Group
     * @param status            Notification status
     * @param startDate         Start date
     * @param endDate           End date
     * @param size              Number of notifications to retrieve
     * @param nextPagesKey      Key to retrieve the next page
     * @return the list of notifications
     */
    public Mono<BffNotificationsResponse> searchReceivedDelegatedNotifications(String xPagopaPnUid,
                                                                               CxTypeAuthFleet xPagopaPnCxType,
                                                                               String xPagopaPnCxId,
                                                                               String iunMatch,
                                                                               List<String> xPagopaPnCxGroups,
                                                                               String senderId,
                                                                               String recipientId,
                                                                               String group,
                                                                               NotificationStatusV26 status,
                                                                               OffsetDateTime startDate,
                                                                               OffsetDateTime endDate,
                                                                               Integer size,
                                                                               String nextPagesKey) {
        log.info("Search delegated notifications - senderId: {} - type: {} - groups: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        Mono<NotificationSearchResponse> notifications = pnDeliveryClient.searchReceivedDelegatedNotifications(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iunMatch,
                xPagopaPnCxGroups,
                senderId,
                recipientId,
                group,
                NotificationStatusMapper.notificationStatusMapper.convertDeliveryRecipientNotificationStatus(status),
                startDate,
                endDate,
                size,
                nextPagesKey
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return notifications.map(NotificationsReceivedMapper.modelMapper::toBffNotificationsResponse);
    }

    /**
     * Get the detail of a notification. This is for a recipient user.
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxId     Receiver id
     * @param iun               Notification IUN
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param mandateId         mandate id. It is required if the user, that is requesting the notification, is a mandate
     * @return the detail of the notification with a specific IUN
     */
    public Mono<BffFullNotificationV1> getNotificationDetail(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                             String xPagopaPnCxId, String xPagopaPnSrcCh, String iun, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails,
                                                             String mandateId) {
        log.info("Get notification detail - senderId: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        Mono<FullReceivedNotificationV25> notificationDetail = pnDeliveryClient.getReceivedNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails,
                mandateId
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return notificationDetail.map(NotificationReceivedDetailMapper.modelMapper::mapReceivedNotificationDetail);
    }

    /**
     * Download the document linked to a notification. This is for a recipient user.
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param documentType      the document type (aar, attachment or legal fact)
     * @param documentIdx       the document index if attachment
     * @param documentId        the document id if aar or legal fact
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param mandateId         mandate id. It is required if the user, that is requesting the notification, is a mandate
     * @return the requested document
     */
    public Mono<BffDocumentDownloadMetadataResponse> getReceivedNotificationDocument(String xPagopaPnUid,
                                                                                     CxTypeAuthFleet xPagopaPnCxType,
                                                                                     String xPagopaPnCxId, String xPagopaPnSrcCh, String iun,
                                                                                     BffDocumentType documentType,
                                                                                     Integer documentIdx,
                                                                                     String documentId,
                                                                                     List<String> xPagopaPnCxGroups,
                                                                                     String xPagopaPnSrcChDetails,
                                                                                     UUID mandateId
    ) {
        log.info("Get notification document - senderId: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        if (documentType == BffDocumentType.ATTACHMENT) {
            if (documentIdx == null) {
                log.error("Attachment idx not found");
                return Mono.error(new PnBffException(
                        "Attachment idx not found",
                        "The attachment idx is missed",
                        HttpStatus.BAD_REQUEST.value(),
                        ERROR_CODE_BFF_DOCUMENTIDNOTFOUND
                ));
            }
            Mono<NotificationAttachmentDownloadMetadataResponse> attachment = pnDeliveryClient.getReceivedNotificationDocument(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    xPagopaPnSrcCh,
                    iun,
                    documentIdx,
                    xPagopaPnCxGroups,
                    xPagopaPnSrcChDetails,
                    mandateId
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
            return attachment.map(NotificationDownloadDocumentMapper.modelMapper::mapReceivedAttachmentDownloadResponse);
        } else if (documentType == BffDocumentType.AAR) {
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
                    mandateId
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
            return document.map(NotificationDownloadDocumentMapper.modelMapper::mapDocumentDownloadResponse);
        } else {
            if (documentId == null) {
                log.error("Legal fact id not found");
                return Mono.error(new PnBffException(
                        "Legal fact id not found",
                        "The legal fact id is missed",
                        HttpStatus.BAD_REQUEST.value(),
                        ERROR_CODE_BFF_DOCUMENTIDNOTFOUND
                ));
            }
            // others legal fact case
            Mono<it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactDownloadMetadataResponse> legalFact = pnDeliveryPushClient.getLegalFact(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertDeliveryPushCXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    iun,
                    documentId,
                    xPagopaPnCxGroups,
                    mandateId
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
            return legalFact.map(NotificationDownloadDocumentMapper.modelMapper::mapLegalFactDownloadResponse);
        }
    }

    /**
     * Get the payment for a notification. This is for a recipient user.
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param attachmentName    Type of the payment (PAGOPA or F24)
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param attachmentIdx     Index of the payment
     * @param mandateId         mandate id. It is required if the user, that is requesting the notification, is a mandate
     * @return the payment for the notification with a specific IUN
     */
    public Mono<BffDocumentDownloadMetadataResponse> getReceivedNotificationPayment(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                    String xPagopaPnCxId, String xPagopaPnSrcCh, String iun, String attachmentName,
                                                                                    List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails, UUID mandateId,
                                                                                    Integer attachmentIdx
    ) {
        log.info("Get notification payment - senderId: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        Mono<NotificationAttachmentDownloadMetadataResponse> notificationDetail = pnDeliveryClient.getReceivedNotificationPayment(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                attachmentName,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails,
                mandateId,
                attachmentIdx
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return notificationDetail.map(NotificationDownloadDocumentMapper.modelMapper::mapReceivedAttachmentDownloadResponse);
    }

    /**
     * Check the AAR QR code.
     *
     * @param xPagopaPnUid       User Identifier
     * @param xPagopaPnCxType    Receiver Type
     * @param xPagopaPnCxId      Receiver id
     * @param bffCheckAarMandate the request to check the AAR QR code
     * @param xPagopaPnCxGroups  Receiver Group id List
     * @return the response of the check
     */
    public Mono<BffCheckAarResponse> checkAarQrCode(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                    String xPagopaPnCxId,
                                                    Mono<BffCheckAarRequest> bffCheckAarMandate,
                                                    List<String> xPagopaPnCxGroups
    ) {
        log.info("Exchange aar from qr code - senderId: {} - type: {} - groups: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        return bffCheckAarMandate.flatMap(requestDto ->
                pnDeliveryClient.checkAarQrCode(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                        xPagopaPnCxId,
                        NotificationAarQrCodeMapper.modelMapper.toRequestCheckAarMandateDto(requestDto),
                        xPagopaPnCxGroups
                ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
        ).map(NotificationAarQrCodeMapper.modelMapper::toBffResponseCheckAarMandateDto);
    }

    /**
     * Check the TPP.
     *
     * @param retrievalId   the id of the retrieval
     * @param sourceChannel the source channel from header xPagopaPnSrcCh
     * @return the response of the check
     */
    public Mono<BffCheckTPPResponse> checkTpp(String retrievalId, String sourceChannel) {
        log.info("Checking TPP - ID: {}, sourceChannel: {}", retrievalId, sourceChannel);
        if (!sourceChannel.equals(CommonUtility.SourceChannel.TPP.name())) {
            return Mono.error(new PnBffException(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "Missing the required parameter 'TPP' in xPagopaPnSrcCh",
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.toString()
            ));
        }

        return pnEmdClient.checkTpp(retrievalId)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
                .map(NotificationRetrievalIdMapper.modelMapper::toBffCheckTPPResponse);
    }
}