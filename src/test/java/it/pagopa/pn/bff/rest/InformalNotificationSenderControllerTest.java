package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.CampaignMapper;
import it.pagopa.pn.bff.mappers.notifications.InformalNotificationSentMapper;
import it.pagopa.pn.bff.mocks.CampaignMock;
import it.pagopa.pn.bff.mocks.InformalNotificationSearchMock;
import it.pagopa.pn.bff.mocks.InformalSentNotificationDetailMock;
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

import java.time.OffsetDateTime;
import java.util.List;

@WebFluxTest(InformalNotificationSenderController.class)
class InformalNotificationSenderControllerTest {

    private static final String INFORMAL_CAMPAIGNS_PATH =
            "/bff/v1/notifications/informal/campaigns";

    private static final Integer SIZE = 10;
    private static final String NEXT_PAGES_KEY = "next-page-key";

    private final CampaignMock campaignMock = new CampaignMock();
    private final InformalNotificationSearchMock informalNotificationSearchMock = new InformalNotificationSearchMock();
    private final InformalSentNotificationDetailMock informalSentNotificationDetailMock = new InformalSentNotificationDetailMock();

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

    @Test
    void searchInformalSentNotification() {
        BffInformalSenderNotificationSearchResponse response =
                CampaignMapper.modelMapper.toBffInformalSenderNotificationSearchResponse(
                        informalNotificationSearchMock.getInformalNotificationSearchResponseMock()
                );

        Mockito.when(informalNotificationSenderService.searchInformalSentNotifications(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(InformalNotificationStatusV1.class),
                Mockito.anyBoolean(),
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PnBffRestConstants.SEARCH_INFORMAL_SENT_NOTIFICATIONS_PATH)
                        .queryParam("startDate", InformalNotificationSearchMock.START_DATE)
                        .queryParam("endDate", InformalNotificationSearchMock.END_DATE)
                        .queryParam("recipientId", InformalNotificationSearchMock.RECIPIENT_ID)
                        .queryParam("iunMatch", InformalNotificationSearchMock.IUN_MATCH)
                        .queryParam("status", InformalNotificationSearchMock.STATUS.getValue())
                        .queryParam("viewed", true)
                        .queryParam("delivered", true)
                        .queryParam("size", InformalNotificationSearchMock.SIZE)
                        .queryParam("nextPagesKey", InformalNotificationSearchMock.NEXT_PAGES_KEY)
                        .build(CampaignMock.CAMPAIGN_ID))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffInformalSenderNotificationSearchResponse.class)
                .isEqualTo(response);

        Mockito.verify(informalNotificationSenderService).searchInformalSentNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                CampaignMock.CAMPAIGN_ID,
                OffsetDateTime.parse(InformalNotificationSearchMock.START_DATE),
                OffsetDateTime.parse(InformalNotificationSearchMock.END_DATE),
                UserMock.PN_CX_GROUPS,
                InformalNotificationSearchMock.RECIPIENT_ID,
                InformalNotificationSearchMock.IUN_MATCH,
                InformalNotificationSearchMock.STATUS,
                true,
                true,
                InformalNotificationSearchMock.SIZE,
                InformalNotificationSearchMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void searchInformalSentNotificationError() {
        Mockito.when(informalNotificationSenderService.searchInformalSentNotifications(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(InformalNotificationStatusV1.class),
                Mockito.anyBoolean(),
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(
                Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND"))
        );

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PnBffRestConstants.SEARCH_INFORMAL_SENT_NOTIFICATIONS_PATH)
                        .queryParam("startDate", InformalNotificationSearchMock.START_DATE)
                        .queryParam("endDate", InformalNotificationSearchMock.END_DATE)
                        .queryParam("recipientId", InformalNotificationSearchMock.RECIPIENT_ID)
                        .queryParam("iunMatch", InformalNotificationSearchMock.IUN_MATCH)
                        .queryParam("status", InformalNotificationSearchMock.STATUS.getValue())
                        .queryParam("viewed", true)
                        .queryParam("delivered", true)
                        .queryParam("size", InformalNotificationSearchMock.SIZE)
                        .queryParam("nextPagesKey", InformalNotificationSearchMock.NEXT_PAGES_KEY)
                        .build(CampaignMock.CAMPAIGN_ID))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(informalNotificationSenderService).searchInformalSentNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                CampaignMock.CAMPAIGN_ID,
                OffsetDateTime.parse(InformalNotificationSearchMock.START_DATE),
                OffsetDateTime.parse(InformalNotificationSearchMock.END_DATE),
                UserMock.PN_CX_GROUPS,
                InformalNotificationSearchMock.RECIPIENT_ID,
                InformalNotificationSearchMock.IUN_MATCH,
                InformalNotificationSearchMock.STATUS,
                true,
                true,
                InformalNotificationSearchMock.SIZE,
                InformalNotificationSearchMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void getSentInformalNotification() {
        BffFullSentInformalNotificationV1 response =
                InformalNotificationSentMapper.modelMapper.mapSentInformalNotificationDetail(
                        informalSentNotificationDetailMock.getFullSentInformalNotificationMock(),
                        List.of(BffNotificationChannelType.IO)
                );

        Mockito.when(informalNotificationSenderService.getSentInformalNotification(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PnBffRestConstants.SENT_INFORMAL_NOTIFICATION_PATH)
                        .build(InformalSentNotificationDetailMock.IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffFullSentInformalNotificationV1.class)
                .isEqualTo(response);

        Mockito.verify(informalNotificationSenderService).getSentInformalNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                InformalSentNotificationDetailMock.IUN,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void getSentInformalNotificationError() {
        Mockito.when(informalNotificationSenderService.getSentInformalNotification(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(
                Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND"))
        );

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PnBffRestConstants.SENT_INFORMAL_NOTIFICATION_PATH)
                        .build(InformalSentNotificationDetailMock.IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(informalNotificationSenderService).getSentInformalNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                InformalSentNotificationDetailMock.IUN,
                UserMock.PN_CX_GROUPS
        );
    }
}