package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponse;
import it.pagopa.pn.bff.mappers.notification.NotificationSentMapper;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.mocks.NotificationSentMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationDetailPaServiceTest {

    private static NotificationDetailPAService notificationDetailPAService;
    private static PnDeliveryClientPAImpl pnDeliveryClientPA;
    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();
    private final NotificationSentMock notificationSentMock = new NotificationSentMock();

    @BeforeAll
    public static void setup() {
        pnDeliveryClientPA = mock(PnDeliveryClientPAImpl.class);
        notificationDetailPAService = new NotificationDetailPAService(pnDeliveryClientPA);
    }

    @Test
    void testGetNotificationDetail() {
        when(pnDeliveryClientPA.getSentNotification(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.just(notificationDetailPaMock.getNotificationMultiRecipientMock()));

        Mono<BffFullNotificationV1> result = notificationDetailPAService.getSentNotificationDetail(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDetailPaMock.getNotificationMultiRecipientMock()))
                .verifyComplete();
    }

    @Test
    void testGetNotificationDetailError() {
        when(pnDeliveryClientPA.getSentNotification(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffFullNotificationV1> result = notificationDetailPAService.getSentNotificationDetail(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void searchSentNotification() {
        when(pnDeliveryClientPA.searchSentNotification(
                Mockito.any(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationSentMock.getNotificationSentPNMock()));

        Mono<BffNotificationsResponse> result = notificationDetailPAService.searchSentNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                UserMock.IUN_MATCH,
                UserMock.SENDER_ID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus.ACCEPTED,
                UserMock.SUBJECT_REG_EXP,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        );

        StepVerifier.create(result)
                .expectNext(NotificationSentMapper.modelMapper.toBffNotificationsResponse(notificationSentMock.getNotificationSentPNMock()))
                .verifyComplete();
    }

    @Test
    void searchSentNotificationError() {
        when(pnDeliveryClientPA.searchSentNotification(
                Mockito.any(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffNotificationsResponse> result = notificationDetailPAService.searchSentNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                UserMock.IUN_MATCH,
                UserMock.SENDER_ID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus.ACCEPTED,
                UserMock.SUBJECT_REG_EXP,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}