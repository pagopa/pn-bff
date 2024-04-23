package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.DowntimeApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPnDowntimeHistoryResponse;
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
        log.logStartingProcess("getCurrentStatusV1");

        Mono<BffPnStatusResponse> serviceResponse = downtimeLogsService.getCurrentStatus()
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);


        log.logEndingProcess("getCurrentStatusV1");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET bff/v1/downtime/history: Application status history
     * Get the history of the events related to application status
     *
     * @param page     page number
     * @param size     number of elements per page
     * @param exchange
     * @return the application status history
     */
    @Override
    public Mono<ResponseEntity<BffPnDowntimeHistoryResponse>> getStatusHistoryV1(String page, String size, final ServerWebExchange exchange) {
        log.logStartingProcess("getStatusHistoryV1");

        Mono<BffPnDowntimeHistoryResponse> serviceResponse = downtimeLogsService.getStatusHistory(page, size)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);


        log.logEndingProcess("getStatusHistoryV1");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}