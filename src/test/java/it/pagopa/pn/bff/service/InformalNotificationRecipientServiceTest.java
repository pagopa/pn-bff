package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.SenderContactInfo;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.InformalNotificationDetailMock;
import it.pagopa.pn.bff.mappers.notifications.NotificationDownloadDocumentMapper;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class InformalNotificationRecipientServiceTest {

    private PnDeliveryClientRecipientImpl pnDeliveryClient;
    private InformalNotificationRecipientService service;
    private InformalNotificationDetailMock notificationMock;
    private NotificationDownloadDocumentMock documentMock;

    @BeforeEach
    void setup() {
        pnDeliveryClient = mock(PnDeliveryClientRecipientImpl.class);
        service = new InformalNotificationRecipientService(
                pnDeliveryClient,
                new PnBffExceptionUtility(new ObjectMapper())
        );
        notificationMock = new InformalNotificationDetailMock();
        documentMock = new NotificationDownloadDocumentMock();
    }

    @Test
    void getInformalNotificationDetailEnrichesSenderContacts() {
        SenderContactInfo contacts = new SenderContactInfo()
                .senderId("pa123")
                .email("sender@example.com")
                .pec("sender@pec.example.com")
                .phone("+390212345678")
                .site("https://example.com");
        mockNotificationDetail();
        when(pnDeliveryClient.getSenderContacts(InformalNotificationDetailMock.SENDER_PA_ID))
                .thenReturn(Mono.just(contacts));

        StepVerifier.create(getInformalNotificationDetail())
                .assertNext(notification -> {
                    assertEquals(contacts.getSenderId(), notification.getSenderContacts().getSenderId());
                    assertEquals(contacts.getEmail(), notification.getSenderContacts().getEmail());
                    assertEquals(contacts.getPec(), notification.getSenderContacts().getPec());
                    assertEquals(contacts.getPhone(), notification.getSenderContacts().getPhone());
                    assertEquals(contacts.getSite(), notification.getSenderContacts().getSite());
                })
                .verifyComplete();

        verify(pnDeliveryClient).getSenderContacts(InformalNotificationDetailMock.SENDER_PA_ID);
    }

    @Test
    void getInformalNotificationDetailIgnoresSenderContactsError() {
        mockNotificationDetail();
        when(pnDeliveryClient.getSenderContacts(InformalNotificationDetailMock.SENDER_PA_ID))
                .thenReturn(Mono.error(new RuntimeException("Sender contacts unavailable")));

        StepVerifier.create(getInformalNotificationDetail())
                .assertNext(notification -> {
                    assertEquals(InformalNotificationDetailMock.IUN, notification.getIun());
                    assertNull(notification.getSenderContacts());
                })
                .verifyComplete();
    }

    @Test
    void getInformalNotificationDetailError() {
        when(pnDeliveryClient.getReceivedInformalNotification(
                Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyList(), Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(getInformalNotificationDetail())
                .expectErrorMatches(error -> error instanceof PnBffException
                        && ((PnBffException) error).getProblem().getStatus() == 404)
                .verify();

        verifyNoInteractionsAfterMainDetailError();
    }

    @Test
    void getInformalNotificationDocument() {
        when(pnDeliveryClient.getReceivedInformalNotificationDocument(
                Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyInt(), Mockito.anyList(), Mockito.anyString()
        )).thenReturn(Mono.just(documentMock.getRecipientAttachmentMock()));

        StepVerifier.create(requestInformalNotificationDocument())
                .expectNext(NotificationDownloadDocumentMapper.modelMapper
                        .mapReceivedAttachmentDownloadResponse(documentMock.getRecipientAttachmentMock()))
                .verifyComplete();
    }

    @Test
    void getInformalNotificationDocumentError() {
        when(pnDeliveryClient.getReceivedInformalNotificationDocument(
                Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyInt(), Mockito.anyList(), Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(requestInformalNotificationDocument())
                .expectErrorMatches(error -> error instanceof PnBffException
                        && ((PnBffException) error).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getInformalNotificationPaymentAttachment() {
        when(pnDeliveryClient.getReceivedInformalNotificationPaymentAttachment(
                Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString(),
                Mockito.anyInt()
        )).thenReturn(Mono.just(documentMock.getRecipientAttachmentMock()));

        StepVerifier.create(requestInformalNotificationPaymentAttachment())
                .expectNext(NotificationDownloadDocumentMapper.modelMapper
                        .mapReceivedAttachmentDownloadResponse(documentMock.getRecipientAttachmentMock()))
                .verifyComplete();
    }

    @Test
    void getInformalNotificationPaymentAttachmentError() {
        when(pnDeliveryClient.getReceivedInformalNotificationPaymentAttachment(
                Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString(),
                Mockito.anyInt()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(requestInformalNotificationPaymentAttachment())
                .expectErrorMatches(error -> error instanceof PnBffException
                        && ((PnBffException) error).getProblem().getStatus() == 404)
                .verify();
    }

    private void mockNotificationDetail() {
        when(pnDeliveryClient.getReceivedInformalNotification(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationMock.getInformalNotificationMock()));
    }

    private Mono<BffFullInformalNotificationV1> getInformalNotificationDetail() {
        return service.getInformalNotificationDetail(
                "user-id",
                CxTypeAuthFleet.PF,
                "cx-id",
                "WEB",
                InformalNotificationDetailMock.IUN,
                List.of("group-id"),
                "details"
        );
    }

    private Mono<BffDocumentDownloadMetadataResponse> requestInformalNotificationDocument() {
        return service.getInformalNotificationDocument(
                UserMock.PN_UID, CxTypeAuthFleet.PF, UserMock.PN_CX_ID,
                NotificationsReceivedMock.SOURCE_CHANNEL, InformalNotificationDetailMock.IUN, 0,
                UserMock.PN_CX_GROUPS, NotificationsReceivedMock.SOURCE_CHANNEL_DETAILS
        );
    }

    private Mono<BffDocumentDownloadMetadataResponse> requestInformalNotificationPaymentAttachment() {
        return service.getInformalNotificationDocument(
                UserMock.PN_UID, CxTypeAuthFleet.PF, UserMock.PN_CX_ID,
                NotificationsReceivedMock.SOURCE_CHANNEL, InformalNotificationDetailMock.IUN,
                "PAGOPA", UserMock.PN_CX_GROUPS, NotificationsReceivedMock.SOURCE_CHANNEL_DETAILS, 0
        );
    }

    private void verifyNoInteractionsAfterMainDetailError() {
        verify(pnDeliveryClient, never()).getSenderContacts(Mockito.anyString());
    }
}
