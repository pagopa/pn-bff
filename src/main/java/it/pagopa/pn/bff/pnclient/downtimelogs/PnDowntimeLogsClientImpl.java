package it.pagopa.pn.bff.pnclient.downtimelogs;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.api.DowntimeApi;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.*;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnDowntimeLogsClientImpl {
    private final DowntimeApi downtimeApi;
    private final String serviceName = "pn-downtime-logs";

    public Mono<PnStatusResponse> getCurrentStatus() {
        log.logInvokingExternalService(serviceName, "currentStatus");
        return downtimeApi.currentStatus()
                ;
    }

    public Mono<PnDowntimeHistoryResponse> getStatusHistory(OffsetDateTime fromTime,
                                                            OffsetDateTime toTime,
                                                            List<PnFunctionality> functionalities,
                                                            String page,
                                                            String size) {
        log.logInvokingExternalService(serviceName, "statusHistory");
        return downtimeApi.statusHistory(fromTime, toTime, functionalities, page, size)
                ;
    }

    public Mono<LegalFactDownloadMetadataResponse> getLegalFact(String legalFactId) {
        return downtimeApi.getLegalFact(legalFactId)
                ;
    }

    public Mono<File> getMalfunctionPreview(MalfunctionLegalFact malfunctionLegalFact) {
        log.logInvokingExternalService(serviceName, "getMalfunctionPreview");
        return downtimeApi.getMalfunctionPreview(malfunctionLegalFact);
    }
}