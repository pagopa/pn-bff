package it.pagopa.pn.bff.rest;


import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationReceivedApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponseV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus;
import it.pagopa.pn.bff.service.NotificationDetailRecipientService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

@CustomLog
@RestController
public class ReceivedNotificationController implements NotificationReceivedApi {

    private final NotificationDetailRecipientService notificationDetailRecipientService;

    public ReceivedNotificationController(NotificationDetailRecipientService notificationDetailRecipientService) {
        this.notificationDetailRecipientService = notificationDetailRecipientService;
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
        log.logStartingProcess("getReceivedNotificationV1");

        Mono<BffFullNotificationV1> serviceResponse = notificationDetailRecipientService.getNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, xPagopaPnCxGroups, mandateId
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);


        log.logEndingProcess("getReceivedNotificationV1");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
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
    public Mono<ResponseEntity<BffNotificationsResponseV1>> searchReceivedNotificationsV1(String xPagopaPnUid,
                                                                                          CxTypeAuthFleet xPagopaPnCxType,
                                                                                          String xPagopaPnCxId,
                                                                                          OffsetDateTime startDate,
                                                                                          OffsetDateTime endDate,
                                                                                          List<String> xPagopaPnCxGroups,
                                                                                          String mandateId,
                                                                                          String senderId,
                                                                                          NotificationStatus status,
                                                                                          String subjectRegExp,
                                                                                          String iunMatch,
                                                                                          Integer size,
                                                                                          String nextPagesKey,
                                                                                          final ServerWebExchange exchange) {
    log.logStartingProcess("searchReceivedNotificationsV1");

    Mono<BffNotificationsResponseV1> serviceResponse = notificationDetailRecipientService.searchReceivedNotification(
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
    ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

    log.logEndingProcess("searchReceivedNotificationsV1");
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
    public Mono<ResponseEntity<BffNotificationsResponseV1>> searchReceivedDelegatedNotificationsV1(String xPagopaPnUid,
                                                                                                   CxTypeAuthFleet xPagopaPnCxType,
                                                                                                   String xPagopaPnCxId,
                                                                                                   OffsetDateTime startDate,
                                                                                                   OffsetDateTime endDate,
                                                                                                   List<String> xPagopaPnCxGroups,
                                                                                                   String senderId,
                                                                                                   String recipientId,
                                                                                                   String group,
                                                                                                   NotificationStatus status,
                                                                                                   String iunMatch,
                                                                                                   Integer size,
                                                                                                   String nextPagesKey,
                                                                                                   final ServerWebExchange exchange) {
    log.logStartingProcess("searchReceivedDelegatedNotificationsV1");

    Mono<BffNotificationsResponseV1> serviceResponse = notificationDetailRecipientService.searchReceivedDelegatedNotification(
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
    ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

    log.logEndingProcess("searchReceivedDelegatedNotificationsV1");
    return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}