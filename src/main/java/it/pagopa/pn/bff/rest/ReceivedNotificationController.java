package it.pagopa.pn.bff.rest;


import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationReceivedApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.NotificationDetailService;
import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class ReceivedNotificationController implements NotificationReceivedApi {

    private final NotificationDetailService notificationDetailService;

    public ReceivedNotificationController(NotificationDetailService notificationDetailService) {
        this.notificationDetailService = notificationDetailService;
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
        serviceResponse = notificationDetailService.getNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups, iun, mandateId
        );

        try {
            log.logEndingProcess("getReceivedNotification");
            return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
        } catch (PnRuntimeException exc) {
            log.info("Exception on getReceivedNotification: {}", exc.getProblem());
            throw exc;
        }
    }

}
