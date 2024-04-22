package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnStatusResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPnStatusResponse;
import it.pagopa.pn.bff.mappers.downtimelogs.StatusResponseMapper;
import it.pagopa.pn.bff.pnclient.downtimelogs.PnDowntimeLogsClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DowntimeLogsService {
    private final PnDowntimeLogsClientImpl pnDowntimeLogsClient;

    /**
     * Get application status
     *
     * @return the status of the application
     */
    public Mono<BffPnStatusResponse> getCurrentStatus() {
        log.info("Get application status");
        Mono<PnStatusResponse> statusResponse = pnDowntimeLogsClient.getCurrentStatus()
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return statusResponse.map(StatusResponseMapper.modelMapper::mapPnStatusResponse);
    }
}