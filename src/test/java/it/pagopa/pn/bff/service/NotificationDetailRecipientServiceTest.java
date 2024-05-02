package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponse;
import it.pagopa.pn.bff.mappers.notification.NotificationReceivedMapper;
import it.pagopa.pn.bff.mappers.notificationdetail.NotificationDetailMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.mocks.NotificationReceivedMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationDetailRecipientServiceTest {

    private static NotificationDetailRecipientService notificationDetailRecipientService;
    private static PnDeliveryClientRecipientImpl pnDeliveryClientRecipient;
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    private final NotificationReceivedMock notificationReceivedMock = new NotificationReceivedMock();

    @BeforeAll
    public static void setup() {
        pnDeliveryClientRecipient = mock(PnDeliveryClientRecipientImpl.class);
        notificationDetailRecipientService = new NotificationDetailRecipientService(pnDeliveryClientRecipient);
    }

    @Test
    void testGetNotificationDetail() {
        when(pnDeliveryClientRecipient.getReceivedNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationDetailRecipientMock.getNotificationMultiRecipientMock()));

        Mono<BffFullNotificationV1> result = notificationDetailRecipientService.getNotificationDetail(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.MANDATE_ID
        );

        StepVerifier.create(result)
                .expectNext(NotificationDetailMapper.modelMapper.mapReceivedNotificationDetail(notificationDetailRecipientMock.getNotificationMultiRecipientMock()))
                .verifyComplete();
    }

    @Test
    void testGetNotificationDetailError() {
        when(pnDeliveryClientRecipient.getReceivedNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffFullNotificationV1> result = notificationDetailRecipientService.getNotificationDetail(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.MANDATE_ID
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void searchReceivedNotification() {
        when(pnDeliveryClientRecipient.searchReceivedNotification(
                Mockito.any(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationReceivedMock.getNotificationReceivedPNMock()));

        Mono<BffNotificationsResponse> result = notificationDetailRecipientService.searchReceivedNotification(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.MANDATE_ID,
                UserMock.SENDER_ID,
                UserMock.STATUS,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SUBJECT_REG_EXP,
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        );

        StepVerifier.create(result)
                .expectNext(NotificationReceivedMapper.modelMapper.toBffNotificationsResponse(notificationReceivedMock.getNotificationReceivedPNMock()))
                .verifyComplete();
    }

    @Test
    void searchReceivedNotificationError() {
        when(pnDeliveryClientRecipient.searchReceivedNotification(
                Mockito.any(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffNotificationsResponse> result = notificationDetailRecipientService.searchReceivedNotification(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.MANDATE_ID,
                UserMock.SENDER_ID,
                UserMock.STATUS,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SUBJECT_REG_EXP,
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void searchReceivedDelegatedNotifications(){
        when(pnDeliveryClientRecipient.searchReceivedDelegatedNotification(
                Mockito.any(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationReceivedMock.getNotificationReceivedPNMock()));

        Mono<BffNotificationsResponse> result = notificationDetailRecipientService.searchReceivedDelegatedNotification(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.SENDER_ID,
                UserMock.RECIPIENT_ID,
                UserMock.GROUP,
                UserMock.STATUS,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        );

        StepVerifier.create(result)
                .expectNext(NotificationReceivedMapper.modelMapper.toBffNotificationsResponse(notificationReceivedMock.getNotificationReceivedPNMock()))
                .verifyComplete();
    }

    @Test
    void searchReceivedDelegatedNotificationsError(){
        when(pnDeliveryClientRecipient.searchReceivedDelegatedNotification(
                Mockito.any(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffNotificationsResponse> result = notificationDetailRecipientService.searchReceivedDelegatedNotification(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.SENDER_ID,
                UserMock.RECIPIENT_ID,
                UserMock.GROUP,
                UserMock.STATUS,
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