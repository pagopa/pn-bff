package it.pagopa.pn.bff.service;


import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.LegalNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.notifications.*;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import it.pagopa.pn.bff.pnclient.emd.PnEmdClientImpl;
import it.pagopa.pn.bff.pnclient.notificationcostservice.PnNotificationCostServiceClientImpl;
import it.pagopa.pn.bff.utils.CommonUtility;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;

import static it.pagopa.pn.bff.exceptions.PnBffExceptionCodes.ERROR_CODE_BFF_DOCUMENTIDNOTFOUND;
import static it.pagopa.pn.bff.utils.NotificationDetailUtility.findRecipientIndex;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationsRecipientService {

    private final PnDeliveryClientRecipientImpl pnDeliveryClient;
    private final PnDeliveryPushClientImpl pnDeliveryPushClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;
    private final PnEmdClientImpl pnEmdClient;
    private final PnNotificationCostServiceClientImpl pnNotificationCostServiceClient;
    private final ReworkItemsService reworkItemsService;

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
     * @param startDate         Start date
     * @param endDate           End date
     * @param subjectRegExp     Regular expression for the subject
     * @param size              Number of notifications to retrieve
     * @param nextPagesKey      Key to retrieve the next page
     * @param communicationType The type of the communication (LEGAL, INFORMAL, ALL)
     * @return the list of notifications
     */
    public Mono<BffFullNotificationsResponse> searchReceivedNotifications(String xPagopaPnUid,
                                                                          CxTypeAuthFleet xPagopaPnCxType,
                                                                          String xPagopaPnCxId,
                                                                          String iunMatch,
                                                                          List<String> xPagopaPnCxGroups,
                                                                          String mandateId,
                                                                          String senderId,
                                                                          OffsetDateTime startDate,
                                                                          OffsetDateTime endDate,
                                                                          String subjectRegExp,
                                                                          Integer size,
                                                                          String nextPagesKey,
                                                                          String communicationType) {
        log.info("Search notifications - senderId: {} - type: {} - groups: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        Mono<FullNotificationSearchResponse> notifications = pnDeliveryClient.searchReceivedNotifications(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                iunMatch,
                xPagopaPnCxGroups,
                mandateId,
                senderId,
                startDate,
                endDate,
                subjectRegExp,
                size,
                nextPagesKey,
                communicationType
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return notifications.map(NotificationsReceivedMapper.modelMapper::toBffFullNotificationsResponse);
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
    public Mono<BffLegalNotificationsResponse> searchReceivedDelegatedNotifications(String xPagopaPnUid,
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

        Mono<LegalNotificationSearchResponse> notifications = pnDeliveryClient.searchReceivedDelegatedNotifications(
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

        return notifications.map(NotificationsReceivedMapper.modelMapper::toBffLegalNotificationsResponse);
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

        return pnDeliveryClient.getReceivedNotification(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                        xPagopaPnCxId, xPagopaPnSrcCh, iun,
                        xPagopaPnCxGroups, xPagopaPnSrcChDetails, mandateId
                )
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
                .flatMap(notification -> reworkItemsService.getReworkItems(notification)
                        .map(reworkItems -> NotificationReceivedDetailMapper.modelMapper
                                .mapReceivedNotificationDetail(notification, reworkItems)))
                .flatMap(notification -> enrichWithCostDetails(notification, iun));
    }

    /**
     * Check if the notification needs to be enriched with cost details or if it can be returned as-is.
     * Returns the notification as-is when:
     * - the recIndex is not found
     * - the recipient has no payments
     * - the notification is not ASYNC / DELIVERY_MODE
     *
     * @param notification the notification to enrich with cost details
     * @param iun          the iun of the notification
     * @return the notification enriched with cost details if needed, otherwise the original notification
     */
    private Mono<BffFullNotificationV1> enrichWithCostDetails(BffFullNotificationV1 notification, String iun) {
        OptionalInt recIndex = findRecipientIndex(notification.getRecipients());

        if (recIndex.isEmpty()) {
            log.warn("No recipient with taxId valorized found for iun: {}", iun);
            return Mono.just(notification);
        }

        if (CollectionUtils.isEmpty(notification.getRecipients().get(recIndex.getAsInt()).getPayments())) {
            return Mono.just(notification);
        }

        boolean isAsyncDeliveryMode = notification.getPagoPaIntMode() != null
                && notification.getPagoPaIntMode().getValue().equals(BffFullNotificationV1.PagoPaIntModeEnum.ASYNC.getValue())
                && notification.getNotificationFeePolicy().getValue().equals(NotificationFeePolicy.DELIVERY_MODE.getValue());

        if (!isAsyncDeliveryMode) {
            BffNotificationCostDetails costDetails = new BffNotificationCostDetails();
            costDetails.setStatus(BffNotificationCostDetails.StatusEnum.UNAVAILABLE);
            notification.setNotificationCostDetails(costDetails);
            return Mono.just(notification);
        }

        return fetchAndApplyCostDetails(notification, iun, recIndex.getAsInt());
    }

    /**
     * Fetch the cost details for a notification and apply them to the notification.
     *
     * @param notification the notification to enrich with cost details
     * @param iun          the iun of the notification
     * @param recIndex     the index of the recipient to fetch the cost details for
     * @return the notification enriched with cost details
     */
    private Mono<BffFullNotificationV1> fetchAndApplyCostDetails(BffFullNotificationV1 notification,
                                                                 String iun, int recIndex) {
        return pnNotificationCostServiceClient.getNotificationCostRecipient(iun, recIndex)
                .map(costResponse -> {
                    BffNotificationCostDetails costDetails = NotificationCostDetailsMapper.modelMapper.mapCostDetails(costResponse);
                    costDetails.setStatus(BffNotificationCostDetails.StatusEnum.OK);

                    notification.setNotificationCostDetails(costDetails);

                    return notification;
                })
                .onErrorResume(WebClientResponseException.class, error -> {
                    log.info("Error fetching cost details for iun: {}, recIndex: {}. Status code: {}, response body: {}",
                            iun, recIndex, error.getStatusCode(), error.getResponseBodyAsString());

                    BffNotificationCostDetails costDetails = new BffNotificationCostDetails();
                    costDetails.setStatus(HttpStatus.NOT_FOUND.equals(error.getStatusCode())
                            ? BffNotificationCostDetails.StatusEnum.UNAVAILABLE
                            : BffNotificationCostDetails.StatusEnum.ERROR);

                    notification.setNotificationCostDetails(costDetails);

                    return Mono.just(notification);
                });
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