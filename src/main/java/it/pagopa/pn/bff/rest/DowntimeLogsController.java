package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.DowntimeApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPnStatusResponse;
import it.pagopa.pn.bff.service.DowntimeLogsService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@CustomLog
@RestController
public class DowntimeLogsController implements DowntimeApi {
    private final DowntimeLogsService downtimeLogsService;

    public DowntimeLogsController(DowntimeLogsService downtimeLogsService) {
        this.downtimeLogsService = downtimeLogsService;
    }

    /**
     * GET bff/v1/downtime/status: Application status
     * Get the current status of the application, based on the status of its functionality
     *
     * @param exchange
     * @return the current status of the application
     */
    @Override
    public Mono<ResponseEntity<BffPnStatusResponse>> getCurrentStatusV1(final ServerWebExchange exchange) {
        log.logStartingProcess("getCurrentStatus");

        Mono<BffPnStatusResponse> serviceResponse = downtimeLogsService.getCurrentStatus()
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);


        log.logEndingProcess("getCurrentStatus");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}