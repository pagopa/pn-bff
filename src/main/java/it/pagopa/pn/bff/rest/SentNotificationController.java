package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationSentApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.NotificationsPAService;
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
public class SentNotificationController implements NotificationSentApi {

    private final NotificationsPAService notificationsPAService;

    public SentNotificationController(NotificationsPAService notificationsPAService) {
        this.notificationsPAService = notificationsPAService;
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
        log.logStartingProcess("getSentNotificationV1");
        Mono<BffFullNotificationV1> serviceResponse = notificationsPAService.getSentNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        log.logEndingProcess("getSentNotificationV1");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}