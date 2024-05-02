package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.mocks.NotificationReceivedMock;
import it.pagopa.pn.bff.mocks.UserMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnDeliveryClientRecipientImpl.class})
@ExtendWith(SpringExtension.class)
class PnDeliveryClientRecipientImplTest {
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    private final NotificationReceivedMock notificationReceivedMock = new NotificationReceivedMock();
    @Autowired
    private PnDeliveryClientRecipientImpl pnDeliveryClientRecipientImpl;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi")
    private RecipientReadApi recipientReadApi;

    @Test
    void getReceivedNotificationV23() throws RestClientException {
        when(recipientReadApi.getReceivedNotificationV23(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationDetailRecipientMock.getNotificationMultiRecipientMock()));

        StepVerifier.create(pnDeliveryClientRecipientImpl.getReceivedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                "MANDATE_ID"
        )).expectNext(notificationDetailRecipientMock.getNotificationMultiRecipientMock()).verifyComplete();
    }

    @Test
    void getReceivedNotificationV23Error() {
        when(recipientReadApi.getReceivedNotificationV23(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientRecipientImpl.getReceivedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                "MANDATE_ID"
        )).expectError(PnBffException.class).verify();
    }

    @Test
    void searchReceivedNotification() {
        when(recipientReadApi.searchReceivedNotification(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationReceivedMock.getNotificationReceivedPNMock()));

        StepVerifier.create(pnDeliveryClientRecipientImpl.searchReceivedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.MANDATE_ID,
                UserMock.SENDER_ID,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SUBJECT_REG_EXP,
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        )).expectNext(notificationReceivedMock.getNotificationReceivedPNMock()).verifyComplete();
    }

    @Test
    void searchReceivedNotificationError() {
        when(recipientReadApi.searchReceivedNotification(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientRecipientImpl.searchReceivedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.MANDATE_ID,
                UserMock.SENDER_ID,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SUBJECT_REG_EXP,
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        )).expectError(PnBffException.class).verify();
    }

    @Test
    void searchReceivedDelegatedNotification() {
        when(recipientReadApi.searchReceivedDelegatedNotification(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationReceivedMock.getNotificationReceivedPNMock()));

        StepVerifier.create(pnDeliveryClientRecipientImpl.searchReceivedDelegatedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.SENDER_ID,
                UserMock.RECIPIENT_ID,
                UserMock.GROUP,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        )).expectNext(notificationReceivedMock.getNotificationReceivedPNMock()).verifyComplete();
    }

    @Test
    void searchReceivedDelegatedNotificationError() {
        when(recipientReadApi.searchReceivedDelegatedNotification(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientRecipientImpl.searchReceivedDelegatedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                UserMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                UserMock.SENDER_ID,
                UserMock.RECIPIENT_ID,
                UserMock.GROUP,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(UserMock.START_DATE),
                OffsetDateTime.parse(UserMock.END_DATE),
                UserMock.SIZE,
                UserMock.NEXT_PAGES_KEY
        )).expectError(PnBffException.class).verify();
    }
}