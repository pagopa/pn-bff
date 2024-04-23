package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnFunctionality;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnStatusResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffLegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPnDowntimeHistoryResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPnStatusResponse;
import it.pagopa.pn.bff.mappers.downtimelogs.DowntimeHistoryResponseMapper;
import it.pagopa.pn.bff.mappers.downtimelogs.LegalFactDownloadResponseMapper;
import it.pagopa.pn.bff.mappers.downtimelogs.StatusResponseMapper;
import it.pagopa.pn.bff.pnclient.downtimelogs.PnDowntimeLogsClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /**
     * Get application status history
     *
     * @param page page to return
     * @param size number of results to return
     * @return the list of the events related to the application status
     */
    public Mono<BffPnDowntimeHistoryResponse> getStatusHistory(String page, String size) {
        log.info("Get application status history");

        OffsetDateTime fromTime = OffsetDateTime.parse("1900-01-01T00:00:00Z");
        OffsetDateTime toTime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        List<PnFunctionality> functionalities = new ArrayList<>(Arrays.asList(PnFunctionality.values()));

        Mono<PnDowntimeHistoryResponse> pnDowntimeHistoryResponse = pnDowntimeLogsClient.getStatusHistory(fromTime, toTime, functionalities, page, size)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return pnDowntimeHistoryResponse.map(DowntimeHistoryResponseMapper.modelMapper::mapPnDowntimeHistoryResponse);
    }

    /**
     * Get the legal fact linked to a downtime event
     *
     * @param legalFactId the legal fact id
     * @return the downtime legal fact
     */
    public Mono<BffLegalFactDownloadMetadataResponse> getLegalFact(String legalFactId) {
        log.info("Get downtime legal fact");

        Mono<LegalFactDownloadMetadataResponse> legalFactDownloadMetadataResponse = pnDowntimeLogsClient.getLegalFact(legalFactId)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return legalFactDownloadMetadataResponse.map(LegalFactDownloadResponseMapper.modelMapper::mapLegalFactDownloadMetadataResponse);
    }
}