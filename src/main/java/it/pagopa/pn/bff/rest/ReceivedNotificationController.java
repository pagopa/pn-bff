package it.pagopa.pn.bff.rest;


import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationReceivedApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.NotificationDetailRecipientService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
}