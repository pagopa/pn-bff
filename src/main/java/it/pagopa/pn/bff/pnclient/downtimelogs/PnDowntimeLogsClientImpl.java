package it.pagopa.pn.bff.pnclient.downtimelogs;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.api.DowntimeApi;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnFunctionality;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnStatusResponse;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

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
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    public Mono<PnDowntimeHistoryResponse> getStatusHistory(OffsetDateTime fromTime,
                                                            OffsetDateTime toTime,
                                                            List<PnFunctionality> functionalities,
                                                            String page,
                                                            String size) {
        log.logInvokingExternalService(serviceName, "statusHistory");
        return downtimeApi.statusHistory(fromTime, toTime, functionalities, page, size)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    public Mono<LegalFactDownloadMetadataResponse> getLegalFact(String legalFactId) {
        log.logInvokingExternalService(serviceName, "getLegalFact");
        return downtimeApi.getLegalFact(legalFactId)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}