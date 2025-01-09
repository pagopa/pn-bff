package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.NewNotificationApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NewNotificationRequestV24;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.api.SenderReadWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatusV26;
import it.pagopa.pn.bff.mocks.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnDeliveryClientPAImpl.class})
@ExtendWith(SpringExtension.class)
class PnDeliveryClientPAImplTest {
    private final NotificationsSentMock notificationsSentMock = new NotificationsSentMock();
    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    private final NewSentNotificationMock newSentNotificationMock = new NewSentNotificationMock();
    @Autowired
    private PnDeliveryClientPAImpl pnDeliveryClientPAImpl;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.SenderReadB2BApi")
    private SenderReadB2BApi senderReadB2BApi;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.api.SenderReadWebApi")
    private SenderReadWebApi senderReadWebApi;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.NewNotificationApi")
    private NewNotificationApi newNotificationApi;

    @Test
    void searchSentNotifications() {
        when(senderReadWebApi.searchSentNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(NotificationStatusV26.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationsSentMock.getNotificationSentPNMock()));

        StepVerifier.create(pnDeliveryClientPAImpl.searchSentNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                OffsetDateTime.parse(NotificationsSentMock.START_DATE),
                OffsetDateTime.parse(NotificationsSentMock.END_DATE),
                UserMock.PN_CX_GROUPS,
                NotificationsSentMock.RECIPIENT_ID,
                NotificationStatusV26.ACCEPTED,
                NotificationsSentMock.SUBJECT_REG_EXP,
                NotificationsSentMock.IUN_MATCH,
                NotificationsSentMock.SIZE,
                NotificationsSentMock.NEXT_PAGES_KEY
        )).expectNext(notificationsSentMock.getNotificationSentPNMock()).verifyComplete();
    }

    @Test
    void searchSentNotificationsError() {
        when(senderReadWebApi.searchSentNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(NotificationStatusV26.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientPAImpl.searchSentNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                OffsetDateTime.parse(NotificationsSentMock.START_DATE),
                OffsetDateTime.parse(NotificationsSentMock.END_DATE),
                UserMock.PN_CX_GROUPS,
                NotificationsSentMock.RECIPIENT_ID,
                NotificationStatusV26.ACCEPTED,
                NotificationsSentMock.SUBJECT_REG_EXP,
                NotificationsSentMock.IUN_MATCH,
                NotificationsSentMock.SIZE,
                NotificationsSentMock.NEXT_PAGES_KEY
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getSentNotificationV26() throws RestClientException {
        when(senderReadB2BApi.getSentNotificationV26(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.just(notificationDetailPaMock.getNotificationMultiRecipientMock()));

        StepVerifier.create(pnDeliveryClientPAImpl.getSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                NotificationsSentMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS
        )).expectNext(notificationDetailPaMock.getNotificationMultiRecipientMock()).verifyComplete();
    }

    @Test
    void getSentNotificationV26Error() {
        when(senderReadB2BApi.getSentNotificationV26(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientPAImpl.getSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                NotificationsSentMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getSentNotificationDocument() throws RestClientException {
        when(senderReadB2BApi.getSentNotificationDocument(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyList()
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getPaAttachmentMock()));

        StepVerifier.create(pnDeliveryClientPAImpl.getSentNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                0,
                UserMock.PN_CX_GROUPS
        )).expectNext(notificationDownloadDocumentMock.getPaAttachmentMock()).verifyComplete();
    }

    @Test
    void getSentNotificationDocumentError() {
        when(senderReadB2BApi.getSentNotificationDocument(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientPAImpl.getSentNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                0,
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getSentNotificationPayment() throws RestClientException {
        when(senderReadB2BApi.getSentNotificationAttachment(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt()
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getPaAttachmentMock()));

        StepVerifier.create(pnDeliveryClientPAImpl.getSentNotificationPayment(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                0,
                "PAGOPA",
                UserMock.PN_CX_GROUPS,
                0
        )).expectNext(notificationDownloadDocumentMock.getPaAttachmentMock()).verifyComplete();
    }

    @Test
    void getSentNotificationPaymentError() {
        when(senderReadB2BApi.getSentNotificationAttachment(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientPAImpl.getSentNotificationPayment(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                0,
                "PAGOPA",
                UserMock.PN_CX_GROUPS,
                0
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void newSentNotification() throws RestClientException {
        when(newNotificationApi.sendNewNotificationV24(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NewNotificationRequestV24.class),
                Mockito.anyList(),
                Mockito.nullable(String.class),
                Mockito.nullable(String.class)
        )).thenReturn(Mono.just(newSentNotificationMock.getNewSentNotificationResponse()));

        StepVerifier.create(pnDeliveryClientPAImpl.newSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                newSentNotificationMock.getNewSentNotificationRequest(),
                UserMock.PN_CX_GROUPS
        )).expectNext(newSentNotificationMock.getNewSentNotificationResponse()).verifyComplete();
    }

    @Test
    void newSentNotificationError() {
        when(newNotificationApi.sendNewNotificationV24(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NewNotificationRequestV24.class),
                Mockito.anyList(),
                Mockito.nullable(String.class),
                Mockito.nullable(String.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientPAImpl.newSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                newSentNotificationMock.getNewSentNotificationRequest(),
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void preSignedUpload() throws RestClientException {
        when(newNotificationApi.presignedUploadRequest(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Flux.fromIterable(newSentNotificationMock.getPreloadResponseMock()));

        StepVerifier.create(pnDeliveryClientPAImpl.preSignedUpload(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                newSentNotificationMock.getPreloadRequestMock()
        )).expectNextSequence(newSentNotificationMock.getPreloadResponseMock()).verifyComplete();
    }

    @Test
    void preSignedUploadError() {
        when(newNotificationApi.presignedUploadRequest(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClientPAImpl.preSignedUpload(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                newSentNotificationMock.getPreloadRequestMock()
        )).expectError(WebClientResponseException.class).verify();
    }
}