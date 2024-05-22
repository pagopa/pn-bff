package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus;
import it.pagopa.pn.bff.mappers.notifications.NotificationAarQrCodeMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
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
import java.util.UUID;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnDeliveryClientRecipientImpl.class})
@ExtendWith(SpringExtension.class)
class PnDeliveryClientRecipientImplTest {
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    @Autowired
    private PnDeliveryClientRecipientImpl pnDeliveryClientRecipientImpl;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi")
    private RecipientReadApi recipientReadApi;

    @Test
    void searchReceivedNotifications() {
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
        )).thenReturn(Mono.just(notificationsReceivedMock.getNotificationReceivedPNMock()));

        StepVerifier.create(pnDeliveryClientRecipientImpl.searchReceivedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.MANDATE_ID,
                NotificationsReceivedMock.SENDER_ID,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SUBJECT_REG_EXP,
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        )).expectNext(notificationsReceivedMock.getNotificationReceivedPNMock()).verifyComplete();
    }

    @Test
    void searchReceivedNotificationsError() {
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

        StepVerifier.create(pnDeliveryClientRecipientImpl.searchReceivedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.MANDATE_ID,
                NotificationsReceivedMock.SENDER_ID,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SUBJECT_REG_EXP,
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void searchReceivedDelegatedNotifications() {
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
        )).thenReturn(Mono.just(notificationsReceivedMock.getNotificationReceivedPNMock()));

        StepVerifier.create(pnDeliveryClientRecipientImpl.searchReceivedDelegatedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.SENDER_ID,
                NotificationsReceivedMock.RECIPIENT_ID,
                NotificationsReceivedMock.GROUP,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        )).expectNext(notificationsReceivedMock.getNotificationReceivedPNMock()).verifyComplete();
    }

    @Test
    void searchReceivedDelegatedNotificationsError() {
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

        StepVerifier.create(pnDeliveryClientRecipientImpl.searchReceivedDelegatedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.SENDER_ID,
                NotificationsReceivedMock.RECIPIENT_ID,
                NotificationsReceivedMock.GROUP,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        )).expectError(WebClientResponseException.class).verify();
    }

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
                NotificationsReceivedMock.IUN_MATCH,
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
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                "MANDATE_ID"
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getReceivedNotificationDocument() throws RestClientException {
        when(recipientReadApi.getReceivedNotificationDocument(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyList(),
                Mockito.any(UUID.class)
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getRecipientAttachmentMock()));

        StepVerifier.create(pnDeliveryClientRecipientImpl.getReceivedNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                0,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectNext(notificationDownloadDocumentMock.getRecipientAttachmentMock()).verifyComplete();
    }

    @Test
    void getReceivedNotificationDocumentError() {
        when(recipientReadApi.getReceivedNotificationDocument(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyList(),
                Mockito.any(UUID.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientRecipientImpl.getReceivedNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                0,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getSentNotificationPayment() throws RestClientException {
        when(recipientReadApi.getReceivedNotificationAttachment(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(UUID.class),
                Mockito.anyInt()
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getRecipientAttachmentMock()));

        StepVerifier.create(pnDeliveryClientRecipientImpl.getReceivedNotificationPayment(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                "PAGOPA",
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID(),
                0
        )).expectNext(notificationDownloadDocumentMock.getRecipientAttachmentMock()).verifyComplete();
    }

    @Test
    void getSentNotificationPaymentError() {
        when(recipientReadApi.getReceivedNotificationAttachment(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(UUID.class),
                Mockito.anyInt()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientRecipientImpl.getReceivedNotificationPayment(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                "PAGOPA",
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID(),
                0
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void checkAarQrCode() {
        when(recipientReadApi.checkAarQrCode(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.just(notificationsReceivedMock.getResponseCheckAarMandateDtoPNMock()));

        StepVerifier.create(pnDeliveryClientRecipientImpl.checkAarQrCode(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationAarQrCodeMapper.modelMapper.toRequestCheckAarMandateDto(notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock()),
                UserMock.PN_CX_GROUPS
        )).expectNext(notificationsReceivedMock.getResponseCheckAarMandateDtoPNMock()).verifyComplete();
    }

    @Test
    void checkAarQrCodeError() {
        when(recipientReadApi.checkAarQrCode(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientRecipientImpl.checkAarQrCode(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationAarQrCodeMapper.modelMapper.toRequestCheckAarMandateDto(notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock()),
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }
}