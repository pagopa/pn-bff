package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.downtime_logs.BffLegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.downtime_logs.BffPnDowntimeHistoryResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.downtime_logs.BffPnStatusResponse;
import it.pagopa.pn.bff.mappers.downtimelogs.DowntimeHistoryResponseMapper;
import it.pagopa.pn.bff.mappers.downtimelogs.LegalFactDownloadResponseMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Slf4j
@WebFluxTest(DowntimeLogsController.class)
class DowntimeLogsControllerTest {
    private final String LEGAL_FACT_ID = "LEGAL_FACT_ID";
    @Autowired
    WebTestClient webTestClient;
    DowntimeLogsMock downtimeLogsMock = new DowntimeLogsMock();
    @MockBean
    private DowntimeLogsService downtimeLogsService;

    @Test
    void getCurrentStatus() {
        BffPnStatusResponse response = StatusResponseMapper.modelMapper.mapPnStatusResponse(downtimeLogsMock.getStatusMockOK());

        Mockito.when(downtimeLogsService.getCurrentStatus())
                .thenReturn(Mono.just(response));

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
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.DOWNTIME_LOGS_PATH + "/status").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(downtimeLogsService).getCurrentStatus();
    }

    @Test
    void getStatusHistory() {
        BffPnDowntimeHistoryResponse response = DowntimeHistoryResponseMapper.modelMapper.mapPnDowntimeHistoryResponse(downtimeLogsMock.getDowntimeHistoryMock());

        Mockito.when(downtimeLogsService.getStatusHistory(
                        Mockito.nullable(OffsetDateTime.class),
                        Mockito.nullable(OffsetDateTime.class),
                        Mockito.anyString(),
                        Mockito.anyString())
                )
                .thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.DOWNTIME_LOGS_PATH + "/history")
                                .queryParam("page", "1")
                                .queryParam("size", "10")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffPnDowntimeHistoryResponse.class)
                .isEqualTo(response);

        Mockito.verify(downtimeLogsService).getStatusHistory(null, null, "1", "10");
    }

    @Test
    void getStatusHistoryError() {
        Mockito.when(downtimeLogsService.getStatusHistory(
                        Mockito.nullable(OffsetDateTime.class),
                        Mockito.nullable(OffsetDateTime.class),
                        Mockito.anyString(),
                        Mockito.anyString())
                )
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.DOWNTIME_LOGS_PATH + "/history")
                                .queryParam("page", "1")
                                .queryParam("size", "10")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(downtimeLogsService).getStatusHistory(null, null, "1", "10");
    }

    @Test
    void getLegalFact() {
        BffLegalFactDownloadMetadataResponse response = LegalFactDownloadResponseMapper.modelMapper.mapLegalFactDownloadMetadataResponse(downtimeLogsMock.getLegalFactMetadataMock());

        Mockito.when(downtimeLogsService.getLegalFact(Mockito.anyString()))
                .thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.DOWNTIME_LOGS_PATH + "/legal-facts/{legalFactId}")
                                .build(LEGAL_FACT_ID))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffLegalFactDownloadMetadataResponse.class)
                .isEqualTo(response);

        Mockito.verify(downtimeLogsService).getLegalFact(LEGAL_FACT_ID);
    }

    @Test
    void getLegalFactError() {
        Mockito.when(downtimeLogsService.getLegalFact(Mockito.anyString()))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.DOWNTIME_LOGS_PATH + "/legal-facts/{legalFactId}")
                                .build(LEGAL_FACT_ID))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(downtimeLogsService).getLegalFact(LEGAL_FACT_ID);
    }
}