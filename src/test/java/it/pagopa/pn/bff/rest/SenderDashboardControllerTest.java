package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.sender_dashboard.BffSenderDashboardDataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.sender_dashboard.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.senderdashboard.SenderDashboardService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(SenderDashboardController.class)
public class SenderDashboardControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private SenderDashboardService senderDashboardService;

    @Test
    void testGetDashboardDataV1Ok() {
        // Arrange
        String cxType = CxTypeAuthFleet.PA.toString();
        String cxId = UserMock.PN_CX_ID;

        BffSenderDashboardDataResponse response = new BffSenderDashboardDataResponse();
        response.setSenderId("test");

        Mockito.when(senderDashboardService.getDashboardData(cxType, cxId, cxType, cxId, null, null))
                .thenReturn(Mono.just(response));

        // Act / Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.SENDER_DASHBOARD_GET_DATA_PATH).build(
                        CxTypeAuthFleet.PA.toString(), UserMock.PN_CX_ID
                ))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testGetDashboardDataV1NotFound() {
        // Arrange
        String cxType = CxTypeAuthFleet.PA.toString();
        String cxId = UserMock.PN_CX_ID;

        Mockito.when(senderDashboardService.getDashboardData(cxType, cxId, cxType, cxId, null, null))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        // Act / Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.SENDER_DASHBOARD_GET_DATA_PATH).build(
                        CxTypeAuthFleet.PA.toString(), UserMock.PN_CX_ID
                ))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

}