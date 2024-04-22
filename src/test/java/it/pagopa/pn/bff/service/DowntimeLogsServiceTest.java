package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnStatusResponse;
import it.pagopa.pn.bff.mappers.downtimelogs.StatusResponseMapper;
import it.pagopa.pn.bff.mocks.DowntimeLogsMock;
import it.pagopa.pn.bff.pnclient.downtimelogs.PnDowntimeLogsClientImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DowntimeLogsServiceTest {

    private static DowntimeLogsService downtimeLogsService;
    private static PnDowntimeLogsClientImpl pnDowntimeLogsClient;
    DowntimeLogsMock downtimeLogsMock = new DowntimeLogsMock();

    @BeforeAll
    public static void setup() {
        pnDowntimeLogsClient = mock(PnDowntimeLogsClientImpl.class);
        downtimeLogsService = new DowntimeLogsService(pnDowntimeLogsClient);
    }

    @Test
    void testCurrentStatus() {
        PnStatusResponse statusResponse = downtimeLogsMock.getStatusMockOK();

        when(pnDowntimeLogsClient.getCurrentStatus(
        )).thenReturn(Mono.just(statusResponse));

        StepVerifier.create(downtimeLogsService.getCurrentStatus())
                .expectNext(StatusResponseMapper.modelMapper.mapPnStatusResponse(statusResponse))
                .verifyComplete();
    }

    @Test
    void testGetTosContentError() {
        when(pnDowntimeLogsClient.getCurrentStatus(
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(downtimeLogsService.getCurrentStatus())
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}