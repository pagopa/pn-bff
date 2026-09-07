package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignDetailResponseV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignSearchResponseV1;
import it.pagopa.pn.bff.mappers.notifications.CampaignMapper;
import it.pagopa.pn.bff.mocks.CampaignMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.InformalNotificationSenderService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(InformalNotificationSenderController.class)
class InformalNotificationSenderControllerTest {

    private static final String INFORMAL_CAMPAIGNS_PATH =
            "/bff/v1/notifications/informal/campaigns";

    private static final Integer SIZE = 10;
    private static final String NEXT_PAGES_KEY = "next-page-key";

    private final CampaignMock campaignMock = new CampaignMock();

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InformalNotificationSenderService informalNotificationSenderService;

    @Test
    void getListCampaigns() {
        BffCampaignSearchResponseV1 response =
                CampaignMapper.modelMapper.toBffCampaignSearchResponse(
                        campaignMock.getCampaignSearchResponseMock()
                );

        Mockito.when(informalNotificationSenderService.getListCampaigns(
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(INFORMAL_CAMPAIGNS_PATH)
                                .queryParam("size", SIZE)
                                .queryParam("nextPagesKey", NEXT_PAGES_KEY)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffCampaignSearchResponseV1.class)
                .isEqualTo(response);

        Mockito.verify(informalNotificationSenderService).getListCampaigns(
                UserMock.PN_CX_ID,
                SIZE,
                NEXT_PAGES_KEY
        );
    }

    @Test
    void getListCampaignsError() {
        Mockito.when(informalNotificationSenderService.getListCampaigns(
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(
                Mono.error(
                        new PnBffException(
                                "Not Found",
                                "Not Found",
                                404,
                                "NOT_FOUND"
                        )
                )
        );

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(INFORMAL_CAMPAIGNS_PATH)
                                .queryParam("size", SIZE)
                                .queryParam("nextPagesKey", NEXT_PAGES_KEY)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(informalNotificationSenderService).getListCampaigns(
                UserMock.PN_CX_ID,
                SIZE,
                NEXT_PAGES_KEY
        );
    }

    @Test
    void getReceivedInformalNotification() {
        BffCampaignDetailResponseV1 response =
                CampaignMapper.modelMapper.mapCampaignDetail(
                        campaignMock.getCampaignDetailMock()
                );

        Mockito.when(informalNotificationSenderService.getCampaignDetail(
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PnBffRestConstants.CAMPAIGN_DETAIL_PATH)
                        .build(CampaignMock.CAMPAIGN_ID))
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffCampaignDetailResponseV1.class)
                .isEqualTo(response);

        Mockito.verify(informalNotificationSenderService).getCampaignDetail(
                CampaignMock.CAMPAIGN_ID,
                UserMock.PN_CX_ID
        );
    }

    @Test
    void getReceivedInformalNotificationError() {
        Mockito.when(informalNotificationSenderService.getCampaignDetail(
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.CAMPAIGN_DETAIL_PATH)
                                .build(CampaignMock.CAMPAIGN_ID)
                )
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(informalNotificationSenderService).getCampaignDetail(
                CampaignMock.CAMPAIGN_ID,
                UserMock.PN_CX_ID
        );
    }
}