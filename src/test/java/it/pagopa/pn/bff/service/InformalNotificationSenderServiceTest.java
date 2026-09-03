package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa_web_campaign.model.CampaignSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCampaignSearchResponseV1;
import it.pagopa.pn.bff.mappers.notifications.CampaignMapper;
import it.pagopa.pn.bff.mocks.CampaignMock;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InformalNotificationSenderServiceTest {

    private static PnDeliveryClientPAImpl pnDeliveryClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;

    private final CampaignMock campaignMock = new CampaignMock();

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
}