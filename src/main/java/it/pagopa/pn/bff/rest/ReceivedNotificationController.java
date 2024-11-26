package it.pagopa.pn.bff.rest;


import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationReceivedApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.service.NotificationsRecipientService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@CustomLog
@RestController
public class ReceivedNotificationController implements NotificationReceivedApi {

    private final NotificationsRecipientService notificationsRecipientService;

    public ReceivedNotificationController(NotificationsRecipientService notificationsRecipientService) {
        this.notificationsRecipientService = notificationsRecipientService;
    }

    /**
     * GET bff/v1/notifications/received: Search received notifications
     * Search the notifications received by the user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxId     Receiver id
     * @param startDate         Start date
     * @param endDate           End date
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param mandateId         mandate id. It is required if the user, that is requesting the notification, is a mandate
     * @param senderId          Sender id
     * @param status            Notification status
     * @param subjectRegExp     Subject regular expression
     * @param iunMatch          IUN match
     * @param size              Page size
     * @param nextPagesKey      Next page key
     * @param exchange
     * @return the list of notifications received by the user
     */
    @Override
    public Mono<ResponseEntity<BffNotificationsResponse>> searchReceivedNotificationsV1(String xPagopaPnUid,
                                                                                        CxTypeAuthFleet xPagopaPnCxType,
                                                                                        String xPagopaPnCxId,
                                                                                        OffsetDateTime startDate,
                                                                                        OffsetDateTime endDate,
                                                                                        List<String> xPagopaPnCxGroups,
                                                                                        String mandateId,
                                                                                        String senderId,
                                                                                        NotificationStatusV26 status,
                                                                                        String subjectRegExp,
                                                                                        String iunMatch,
                                                                                        Integer size,
                                                                                        String nextPagesKey,
                                                                                        final ServerWebExchange exchange) {

        Mono<BffNotificationsResponse> serviceResponse = notificationsRecipientService.searchReceivedNotifications(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iunMatch,
                xPagopaPnCxGroups,
                mandateId,
                senderId,
                status,
                startDate,
                endDate,
                subjectRegExp,
                size,
                nextPagesKey
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET bff/v1/notifications/received/delegated: Search received delegated notifications
     * Search the notifications received by the user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxId     Receiver id
     * @param startDate         Start date
     * @param endDate           End date
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param senderId          Sender id
     * @param recipientId       Recipient id
     * @param group             Group
     * @param status            Notification status
     * @param iunMatch          IUN match
     * @param size              Page size
     * @param nextPagesKey      Next page key
     * @param exchange
     * @return the list of notifications received by the user
     */
    @Override
    public Mono<ResponseEntity<BffNotificationsResponse>> searchReceivedDelegatedNotificationsV1(String xPagopaPnUid,
                                                                                                 CxTypeAuthFleet xPagopaPnCxType,
                                                                                                 String xPagopaPnCxId,
                                                                                                 OffsetDateTime startDate,
                                                                                                 OffsetDateTime endDate,
                                                                                                 List<String> xPagopaPnCxGroups,
                                                                                                 String senderId,
                                                                                                 String recipientId,
                                                                                                 String group,
                                                                                                 NotificationStatusV26 status,
                                                                                                 String iunMatch,
                                                                                                 Integer size,
                                                                                                 String nextPagesKey,
                                                                                                 final ServerWebExchange exchange) {

        Mono<BffNotificationsResponse> serviceResponse = notificationsRecipientService.searchReceivedDelegatedNotifications(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iunMatch,
                xPagopaPnCxGroups,
                senderId,
                recipientId,
                group,
                status,
                startDate,
                endDate,
                size,
                nextPagesKey
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET bff/v1/notifications/received/{iun}: Notification detail
     * Get the detail of a notification. This is for a recipient user.
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxId     Receiver id
     * @param iun               Notification IUN
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param exchange
     * @param mandateId         mandate id. It is required if the user, that is requesting the notification, is a mandate
     * @return the detail of the notification with a specific IUN
     */
    @Override
    public Mono<ResponseEntity<BffFullNotificationV1>> getReceivedNotificationV1(String xPagopaPnUid,
                                                                                 CxTypeAuthFleet xPagopaPnCxType,
                                                                                 String xPagopaPnCxId,
                                                                                 String iun,
                                                                                 List<String> xPagopaPnCxGroups,
                                                                                 String mandateId,
                                                                                 final ServerWebExchange exchange) {

        Mono<BffFullNotificationV1> serviceResponse = notificationsRecipientService.getNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, xPagopaPnCxGroups, mandateId
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET bff/v1/notifications/received/{iun}/documents/{documentType}: Notification document
     * Download the document linked to a notification
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
    @Override
    public Mono<ResponseEntity<BffDocumentDownloadMetadataResponse>> getReceivedNotificationDocumentV1(String xPagopaPnUid,
                                                                                                       CxTypeAuthFleet xPagopaPnCxType,
                                                                                                       String xPagopaPnCxId,
                                                                                                       String iun,
                                                                                                       BffDocumentType documentType,
                                                                                                       List<String> xPagopaPnCxGroups,
                                                                                                       UUID mandateId,
                                                                                                       Integer documentIdx,
                                                                                                       String documentId,
                                                                                                       final ServerWebExchange exchange) {

        Mono<BffDocumentDownloadMetadataResponse> serviceResponse = notificationsRecipientService.getReceivedNotificationDocument(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, documentType, documentIdx, documentId,
                xPagopaPnCxGroups, mandateId
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET /bff/v1/notifications/received/{iun}/payments/{attachmentName}: Notification payment
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
    @Override
    public Mono<ResponseEntity<BffDocumentDownloadMetadataResponse>> getReceivedNotificationPaymentV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                                      String xPagopaPnCxId, String iun,
                                                                                                      String attachmentName, List<String> xPagopaPnCxGroups,
                                                                                                      UUID mandateId, Integer attachmentIdx,
                                                                                                      final ServerWebExchange exchange) {

        Mono<BffDocumentDownloadMetadataResponse> serviceResponse = notificationsRecipientService.getReceivedNotificationPayment(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, attachmentName, xPagopaPnCxGroups, mandateId, attachmentIdx
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * POST bff/v1/notifications/received/check-aar-qr-code: Check AAR QR Code
     * Check the AAR QR Code
     *
     * @param xPagopaPnUid       User Identifier
     * @param xPagopaPnCxType    Receiver Type
     * @param xPagopaPnCxId      Receiver id
     * @param bffCheckAarRequest Request to check AAR mandate
     * @param xPagopaPnCxGroups  Receiver Group id List
     * @param exchange
     * @return the response of the check AAR mandate
     */
    @Override
    public Mono<ResponseEntity<BffCheckAarResponse>> checkAarQrCodeV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, Mono<BffCheckAarRequest> bffCheckAarRequest, List<String> xPagopaPnCxGroups, ServerWebExchange exchange) {

        Mono<BffCheckAarResponse> serviceResponse = notificationsRecipientService.checkAarQrCode(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, bffCheckAarRequest, xPagopaPnCxGroups);

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}