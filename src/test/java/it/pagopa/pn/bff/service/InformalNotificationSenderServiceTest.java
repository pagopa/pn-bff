package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.InformalNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignDetail;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignDetailResponseV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignSearchResponseV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffInformalSenderNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.notifications.CampaignMapper;
import it.pagopa.pn.bff.mappers.notifications.InformalNotificationSentMapper;
import it.pagopa.pn.bff.mocks.CampaignMock;
import it.pagopa.pn.bff.mocks.InformalNotificationSearchMock;
import it.pagopa.pn.bff.mocks.InformalSentNotificationDetailMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InformalNotificationSenderServiceTest {

    private static PnDeliveryClientPAImpl pnDeliveryClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;

    private final CampaignMock campaignMock = new CampaignMock();
    private final InformalNotificationSearchMock informalNotificationSearchMock = new InformalNotificationSearchMock();
    private final InformalSentNotificationDetailMock informalSentNotificationDetailMock = new InformalSentNotificationDetailMock();

    private InformalNotificationSenderService informalNotificationSenderService;

    @BeforeAll
    void setup() {
        pnDeliveryClient = mock(PnDeliveryClientPAImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());

        informalNotificationSenderService =
                new InformalNotificationSenderService(
                        pnDeliveryClient,
                        pnBffExceptionUtility
                );
    }

    @Test
    void getListCampaigns() {
        CampaignSearchResponse campaignSearchResponse =
                campaignMock.getCampaignSearchResponseMock();

        when(pnDeliveryClient.listCampaigns(
                Mockito.any(UUID.class),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(campaignSearchResponse));

        BffCampaignSearchResponseV1 expected =
                CampaignMapper.modelMapper.toBffCampaignSearchResponse(
                        campaignSearchResponse
                );

        Mono<BffCampaignSearchResponseV1> result =
                informalNotificationSenderService.getListCampaigns(
                        CampaignMock.SENDER_ID,
                        10,
                        "next-page-key"
                );

        StepVerifier.create(result)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void getListCampaignsError() {
        when(pnDeliveryClient.listCampaigns(
                Mockito.any(UUID.class),
                Mockito.anyInt(),
                Mockito.nullable(String.class)
        )).thenReturn(
                Mono.error(
                        new WebClientResponseException(
                                404,
                                "Not Found",
                                null,
                                null,
                                null
                        )
                )
        );

        Mono<BffCampaignSearchResponseV1> result =
                informalNotificationSenderService.getListCampaigns(
                        CampaignMock.SENDER_ID,
                        10,
                        null
                );

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof PnBffException
                                && ((PnBffException) throwable)
                                .getProblem()
                                .getStatus() == 404
                )
                .verify();
    }

    @Test
    void getCampaignDetail() {
        CampaignDetail campaignDetailResponse = campaignMock.getCampaignDetailMock();

        when(pnDeliveryClient.getCampaignDetail(Mockito.anyString(), Mockito.any(UUID.class)))
                .thenReturn(Mono.just(campaignDetailResponse));

        BffCampaignDetailResponseV1 expected = CampaignMapper.modelMapper.mapCampaignDetail(campaignDetailResponse);

        Mono<BffCampaignDetailResponseV1> result =
                informalNotificationSenderService.getCampaignDetail(
                        CampaignMock.CAMPAIGN_ID,
                        CampaignMock.SENDER_ID
                );

        StepVerifier.create(result)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void getCampaignDetailError() {
        when(pnDeliveryClient.getCampaignDetail(Mockito.anyString(), Mockito.any(UUID.class)))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffCampaignDetailResponseV1> result =
                informalNotificationSenderService.getCampaignDetail(
                        CampaignMock.CAMPAIGN_ID,
                        CampaignMock.SENDER_ID
                );

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof PnBffException
                                && ((PnBffException) throwable)
                                .getProblem()
                                .getStatus() == 404
                )
                .verify();
    }

    @Test
    void searchInformalSentNotifications() {
        InformalNotificationSearchResponse informalNotificationSearchResponse =
                informalNotificationSearchMock.getInformalNotificationSearchResponseMock();

        when(pnDeliveryClient.searchInformalSentNotifications(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.InformalNotificationStatusV1.class),
                Mockito.anyBoolean(),
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(informalNotificationSearchResponse));

        BffInformalSenderNotificationSearchResponse expected =
                CampaignMapper.modelMapper.toBffInformalSenderNotificationSearchResponse(
                        informalNotificationSearchResponse
                );

        Mono<BffInformalSenderNotificationSearchResponse> result =
                informalNotificationSenderService.searchInformalSentNotifications(
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

        StepVerifier.create(result)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void searchInformalSentNotificationsError() {
        when(pnDeliveryClient.searchInformalSentNotifications(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.InformalNotificationStatusV1.class),
                Mockito.anyBoolean(),
                Mockito.anyBoolean(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(
                Mono.error(
                        new WebClientResponseException(
                                404,
                                "Not Found",
                                null,
                                null,
                                null
                        )
                )
        );

        Mono<BffInformalSenderNotificationSearchResponse> result =
                informalNotificationSenderService.searchInformalSentNotifications(
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

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof PnBffException
                                && ((PnBffException) throwable)
                                .getProblem()
                                .getStatus() == 404
                )
                .verify();
    }

    @Test
    void getSentInformalNotification() {
        FullSentInformalNotificationV1 fullSentInformalNotification =
                informalSentNotificationDetailMock.getFullSentInformalNotificationMock();

        when(pnDeliveryClient.getSentInformalNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.just(fullSentInformalNotification));

        BffFullSentInformalNotificationV1 expected =
                InformalNotificationSentMapper.modelMapper.mapSentInformalNotificationDetail(fullSentInformalNotification);

        Mono<BffFullSentInformalNotificationV1> result =
                informalNotificationSenderService.getSentInformalNotification(
                        UserMock.PN_UID,
                        CxTypeAuthFleet.PA,
                        UserMock.PN_CX_ID,
                        InformalSentNotificationDetailMock.IUN,
                        UserMock.PN_CX_GROUPS
                );

        StepVerifier.create(result)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void getSentInformalNotificationError() {
        when(pnDeliveryClient.getSentInformalNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(
                Mono.error(
                        new WebClientResponseException(
                                404,
                                "Not Found",
                                null,
                                null,
                                null
                        )
                )
        );

        Mono<BffFullSentInformalNotificationV1> result =
                informalNotificationSenderService.getSentInformalNotification(
                        UserMock.PN_UID,
                        CxTypeAuthFleet.PA,
                        UserMock.PN_CX_ID,
                        InformalSentNotificationDetailMock.IUN,
                        UserMock.PN_CX_GROUPS
                );

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof PnBffException
                                && ((PnBffException) throwable)
                                .getProblem()
                                .getStatus() == 404
                )
                .verify();
    }
}