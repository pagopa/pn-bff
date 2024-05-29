package it.pagopa.pn.bff.pnclient.downtimelogs;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.api.DowntimeApi;
import it.pagopa.pn.bff.mocks.DowntimeLogsMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnDowntimeLogsClientImpl.class})
@ExtendWith(SpringExtension.class)
class PnDowntimeLogsClientImplTest {
    private final DowntimeLogsMock downtimeLogsMock = new DowntimeLogsMock();
    private final String LEGAL_FACT_ID = "LEGAL_FACT_ID";
    @Autowired
    private PnDowntimeLogsClientImpl pnDowntimeLogsClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.api.DowntimeApi")
    private DowntimeApi downtimeApi;

    @Test
    void getCurrentStatus() throws RestClientException {
        when(downtimeApi.currentStatus()).thenReturn(Mono.just(downtimeLogsMock.getStatusMockOK()));

        StepVerifier.create(pnDowntimeLogsClient.getCurrentStatus()).expectNext(downtimeLogsMock.getStatusMockOK()).verifyComplete();
    }

    @Test
    void getCurrentStatusError() {
        when(downtimeApi.currentStatus()).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDowntimeLogsClient.getCurrentStatus()).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getStatusHistory() throws RestClientException {
        when(downtimeApi.statusHistory(
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.just(downtimeLogsMock.getDowntimeHistoryMock()));

        StepVerifier.create(pnDowntimeLogsClient.getStatusHistory(
                OffsetDateTime.parse("1900-01-01T00:00:00Z"),
                OffsetDateTime.now(ZoneOffset.UTC),
                downtimeLogsMock.getFunctionalityMock(),
                "0",
                "10"
        )).expectNext(downtimeLogsMock.getDowntimeHistoryMock()).verifyComplete();
    }

    @Test
    void getStatusHistoryError() {
        when(downtimeApi.statusHistory(
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDowntimeLogsClient.getStatusHistory(
                OffsetDateTime.parse("1900-01-01T00:00:00Z"),
                OffsetDateTime.now(ZoneOffset.UTC),
                downtimeLogsMock.getFunctionalityMock(),
                "0",
                "10"
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getLegalFactHistory() throws RestClientException {
        when(downtimeApi.getLegalFact(
                Mockito.anyString()
        )).thenReturn(Mono.just(downtimeLogsMock.getLegalFactMetadataMock()));

        StepVerifier.create(pnDowntimeLogsClient.getLegalFact(
                LEGAL_FACT_ID
        )).expectNext(downtimeLogsMock.getLegalFactMetadataMock()).verifyComplete();
    }

    @Test
    void getLegalFactError() {
        when(downtimeApi.getLegalFact(
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDowntimeLogsClient.getLegalFact(
                LEGAL_FACT_ID
        )).expectError(WebClientResponseException.class).verify();
    }
}