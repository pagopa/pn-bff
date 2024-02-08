package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.NotificationDetailPAService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class SentNotificationController implements SenderReadB2BApi {

    private final NotificationDetailPAService notificationDetailPAService;

    public SentNotificationController(NotificationDetailPAService notificationDetailPAService) {
        this.notificationDetailPAService = notificationDetailPAService;
    }

    @Override
    public Mono<ResponseEntity<BffFullSentNotificationV23>> getSentNotificationV23(String xPagopaPnUid,
                                                                                   CxTypeAuthFleet xPagopaPnCxType,
                                                                                   String xPagopaPnCxId,
                                                                                   String iun,
                                                                                   List<String> xPagopaPnCxGroups,
                                                                                   final ServerWebExchange exchange) {
        log.logStartingProcess("getSentNotificationV23");
        Mono<BffFullSentNotificationV23> serviceResponse;
        serviceResponse = notificationDetailPAService.getSentNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, xPagopaPnCxGroups
        );

        log.logEndingProcess("getSentNotificationV23");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}
