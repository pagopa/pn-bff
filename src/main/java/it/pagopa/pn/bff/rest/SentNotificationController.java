package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationSentApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.service.NotificationsPAService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

@CustomLog
@RestController
public class SentNotificationController implements NotificationSentApi {

    private final NotificationsPAService notificationsPAService;

    public SentNotificationController(NotificationsPAService notificationsPAService) {
        this.notificationsPAService = notificationsPAService;
    }

    /**
     * GET bff/v1/notifications/sent: Search sent notifications
     * Search the notifications sent by a Public Administration
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param startDate         Start date
     * @param endDate           End date
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param recipientId       Recipient id
     * @param status            Notification status
     * @param subjectRegExp     Regular expression for the subject
     * @param iunMatch          IUN match
     * @param size              Page size
     * @param nextPagesKey      Next page key
     * @param exchange
     * @return the list of notifications sent by a Public Administration
     */
    @Override
    public Mono<ResponseEntity<BffNotificationsResponse>> searchSentNotificationsV1(String xPagopaPnUid,
                                                                                    CxTypeAuthFleet xPagopaPnCxType,
                                                                                    String xPagopaPnCxId,
                                                                                    OffsetDateTime startDate,
                                                                                    OffsetDateTime endDate,
                                                                                    List<String> xPagopaPnCxGroups,
                                                                                    String recipientId,
                                                                                    NotificationStatus status,
                                                                                    String subjectRegExp,
                                                                                    String iunMatch,
                                                                                    Integer size,
                                                                                    String nextPagesKey,
                                                                                    final ServerWebExchange exchange) {

        Mono<BffNotificationsResponse> serviceResponse = notificationsPAService.searchSentNotifications(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups, iunMatch, recipientId, status,
                subjectRegExp, startDate, endDate, size, nextPagesKey
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET bff/v1/notifications/sent/{iun}: Notification detail
     * Get the detail of a notification. This is for a Public Administration user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param exchange
     * @return the detail of the notification with a specific IUN
     */
    @Override
    public Mono<ResponseEntity<BffFullNotificationV1>> getSentNotificationV1(String xPagopaPnUid,
                                                                             CxTypeAuthFleet xPagopaPnCxType,
                                                                             String xPagopaPnCxId,
                                                                             String iun,
                                                                             List<String> xPagopaPnCxGroups,
                                                                             final ServerWebExchange exchange) {

        Mono<BffFullNotificationV1> serviceResponse = notificationsPAService.getSentNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET bff/v1/notifications/sent/{iun}/documents/{documentType}: Notification document
     * Download the document linked to a notification
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param documentType      the document type (aar, attachment or legal fact)
     * @param documentIdx       the document index if attachment
     * @param documentId        the document id if aar or legal fact
     * @param documentCategory  the legal fact category (required only if the documentType is legal fact)
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the requested document
     */
    @Override
    public Mono<ResponseEntity<BffDocumentDownloadMetadataResponse>> getSentNotificationDocumentV1(String xPagopaPnUid,
                                                                                                   CxTypeAuthFleet xPagopaPnCxType,
                                                                                                   String xPagopaPnCxId,
                                                                                                   String iun,
                                                                                                   BffDocumentType documentType,
                                                                                                   List<String> xPagopaPnCxGroups,
                                                                                                   Integer documentIdx,
                                                                                                   String documentId,
                                                                                                   LegalFactCategory documentCategory,
                                                                                                   final ServerWebExchange exchange) {

        Mono<BffDocumentDownloadMetadataResponse> serviceResponse = notificationsPAService.getSentNotificationDocument(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, documentType, documentIdx, documentId,
                documentCategory, xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET /bff/v1/notifications/sent/{iun}/payments/{recipientIdx}/{attachmentName}: Notification payment
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
    @Override
    public Mono<ResponseEntity<BffDocumentDownloadMetadataResponse>> getSentNotificationPaymentV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                                  String xPagopaPnCxId, String iun,
                                                                                                  Integer recipientIdx, String attachmentName,
                                                                                                  List<String> xPagopaPnCxGroups, Integer attachmentIdx,
                                                                                                  final ServerWebExchange exchange) {

        Mono<BffDocumentDownloadMetadataResponse> serviceResponse = notificationsPAService.getSentNotificationPayment(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, recipientIdx, attachmentName, xPagopaPnCxGroups, attachmentIdx
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * PUT bff/v1/notifications/sent/{iun}/cancel: Notification cancellation
     * Cancel a notification
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param exchange
     * @return the status of the operation
     */
    @Override
    public Mono<ResponseEntity<BffRequestStatus>> notificationCancellationV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String iun, List<String> xPagopaPnCxGroups, ServerWebExchange exchange) {

        Mono<BffRequestStatus> serviceResponse = notificationsPAService.notificationCancellation(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.ACCEPTED).body(response));
    }

    /**
     * POST bff/v1/notifications/sent: Create new notification
     *
     * @param xPagopaPnUid           User Identifier
     * @param xPagopaPnCxType        Public Administration Type
     * @param xPagopaPnCxId          Public Administration id
     * @param newNotificationRequest The request that contains the notification to create
     * @param xPagopaPnCxGroups      Public Administration Group id List
     * @param exchange
     * @return the request of the newly created notification
     */
    @Override
    public Mono<ResponseEntity<BffNewNotificationResponse>> newSentNotificationV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, Mono<BffNewNotificationRequest> newNotificationRequest, List<String> xPagopaPnCxGroups, ServerWebExchange exchange) {

        Mono<BffNewNotificationResponse> serviceResponse = notificationsPAService.newSentNotification(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, newNotificationRequest, xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.ACCEPTED).body(response));
    }

    /**
     * POST bff/v1/notifications/sent/documents/upload: Upload one or more documents
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param bffPreLoadRequest Request to get the pre-signed urls
     * @param exchange
     * @return the request of the newly created notification
     */
    @Override
    public Mono<ResponseEntity<Flux<BffPreLoadResponse>>> preSignedUploadV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, Flux<BffPreLoadRequest> bffPreLoadRequest, ServerWebExchange exchange) {

        Flux<BffPreLoadResponse> serviceResponse = notificationsPAService.preSignedUpload(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, bffPreLoadRequest
        );
        
        return serviceResponse
                .collectList()
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(response)));
    }
}