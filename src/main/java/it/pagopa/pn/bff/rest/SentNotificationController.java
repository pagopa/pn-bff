package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationSentApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullSentNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.NotificationDetailPAService;
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

    private final NotificationDetailPAService notificationDetailPAService;

    public SentNotificationController(NotificationDetailPAService notificationDetailPAService) {
        this.notificationDetailPAService = notificationDetailPAService;
    }

    @Override
    public Mono<ResponseEntity<BffFullSentNotificationV1>> getSentNotificationV1(String xPagopaPnUid,
                                                                                  CxTypeAuthFleet xPagopaPnCxType,
                                                                                  String xPagopaPnCxId,
                                                                                  String iun,
                                                                                  List<String> xPagopaPnCxGroups,
                                                                                  final ServerWebExchange exchange) {
        log.logStartingProcess("getSentNotificationV1");
        Mono<BffFullSentNotificationV1> serviceResponse;
        serviceResponse = notificationDetailPAService.getSentNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        log.logEndingProcess("getSentNotificationV1");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}