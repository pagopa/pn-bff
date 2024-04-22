package it.pagopa.pn.bff.pnclient.downtimelogs;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.api.DowntimeApi;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnStatusResponse;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnDowntimeLogsClientImpl {
    private final DowntimeApi downtimeApi;

    public Mono<PnStatusResponse> getCurrentStatus() {
        log.logInvokingExternalService("pn-downtime-logs", "currentStatus");
        return downtimeApi.currentStatus()
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}