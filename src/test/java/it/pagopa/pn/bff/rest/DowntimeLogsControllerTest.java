package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPnStatusResponse;
import it.pagopa.pn.bff.mappers.downtimelogs.StatusResponseMapper;
import it.pagopa.pn.bff.mocks.DowntimeLogsMock;
import it.pagopa.pn.bff.service.DowntimeLogsService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@WebFluxTest(DowntimeLogsController.class)
public class DowntimeLogsControllerTest {
    @Autowired
    WebTestClient webTestClient;
    DowntimeLogsMock downtimeLogsMock = new DowntimeLogsMock();
    @MockBean
    private DowntimeLogsService downtimeLogsService;
    @SpyBean
    private DowntimeLogsController downtimeLogsController;

    @Test
    void getCurrentStatus() {
        BffPnStatusResponse response = StatusResponseMapper.modelMapper.mapPnStatusResponse(downtimeLogsMock.getStatusMockOK());

        Mockito.when(downtimeLogsService.getCurrentStatus())
                .thenReturn(Mono.just(response));

        // response.setLastCheckTimestamp(response.getLastCheckTimestamp().atZoneSameInstant(ZoneOffset.UTC));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.DOWNTIME_LOGS_PATH + "/status").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffPnStatusResponse.class)
                .isEqualTo(response);

        Mockito.verify(downtimeLogsService).getCurrentStatus();
    }

    @Test
    void getCurrentStatusError() {
        Mockito.when(downtimeLogsService.getCurrentStatus())
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.DOWNTIME_LOGS_PATH + "/status").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(downtimeLogsService).getCurrentStatus();
    }
}