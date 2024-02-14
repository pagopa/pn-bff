package it.pagopa.pn.bff.rest;


import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationReceivedApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV23;
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

    @Override
    public Mono<ResponseEntity<BffFullReceivedNotificationV23>> getReceivedNotification(String xPagopaPnUid,
                                                                                        CxTypeAuthFleet xPagopaPnCxType,
                                                                                        String xPagopaPnCxId,
                                                                                        String iun,
                                                                                        List<String> xPagopaPnCxGroups,
                                                                                        String mandateId,
                                                                                        final ServerWebExchange exchange) {
        log.logStartingProcess("getReceivedNotification");
        Mono<BffFullReceivedNotificationV23> serviceResponse;
        serviceResponse = notificationDetailRecipientService.getNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups, iun, mandateId
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);


        log.logEndingProcess("getReceivedNotification");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}
