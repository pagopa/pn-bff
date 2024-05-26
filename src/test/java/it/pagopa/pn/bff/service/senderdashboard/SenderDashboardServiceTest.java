package it.pagopa.pn.bff.service.senderdashboard;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardDataResponse;
import it.pagopa.pn.bff.service.senderdashboard.exceptions.SenderNotFoundException;
import it.pagopa.pn.bff.service.senderdashboard.resources.DatalakeS3Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SenderDashboardServiceTest {
    @Mock
    private DatalakeS3Resource datalakeS3Resource;

    @InjectMocks
    private SenderDashboardService senderDashboardService;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInvalidDates() {
        // Arrange
        LocalDate startDate = LocalDate.parse("2024-05-08");
        LocalDate endDate = LocalDate.parse("2022-05-08");

        // Act
        Mono<BffSenderDashboardDataResponse> result = senderDashboardService.getDashboardData(
                "test", "test", "test", "test", startDate, endDate);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400)
                .verify();
    }

    @Test
    public void testCxMismatch() {
        // Arrange / Act
        Mono<BffSenderDashboardDataResponse> result = senderDashboardService.getDashboardData(
                "test", "test1", "test", "test", null, null);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400)
                .verify();
    }

    @Test
    public void testGetData() throws SenderNotFoundException {
        // Arrange
        BffSenderDashboardDataResponse res = new BffSenderDashboardDataResponse();
        res.setSenderId("test");
        when(datalakeS3Resource.getDataResponse("test", null, null)).thenReturn(res);

        // Act
        Mono<BffSenderDashboardDataResponse> result = senderDashboardService.getDashboardData(
                "test", "test", "test", "test", null, null);

        // Assert
        assertEquals(Objects.requireNonNull(result.block()).getSenderId(), "test");
    }
}
