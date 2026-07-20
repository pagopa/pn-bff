package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.SenderContactInfo;
import it.pagopa.pn.bff.mappers.notifications.InformalNotificationReceivedMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationDownloadDocumentMapper;
import it.pagopa.pn.bff.mocks.InformalNotificationDetailMock;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.InformalNotificationRecipientService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static it.pagopa.pn.bff.utils.PnBffRestConstants.SOURCE_CHANNEL_DETAILS_HEADER;
import static it.pagopa.pn.bff.utils.PnBffRestConstants.SOURCE_CHANNEL_HEADER;

@WebFluxTest(InformalNotificationRecipientController.class)
class InformalNotificationRecipientControllerTest {

    private static final String INFORMAL_NOTIFICATION_RECEIVED_PATH =
            "/bff/v1/notifications/informal/received/{iun}";
    private static final String INFORMAL_NOTIFICATION_DOCUMENT_PATH =
            "/bff/v1/notifications/informal/received/{iun}/attachments/documents/{docIdx}";
    private static final String INFORMAL_NOTIFICATION_PAYMENT_PATH =
            "/bff/v1/notifications/informal/received/{iun}/attachments/payment/{attachmentName}";
    private static final String IUN = "ABCD-EFGH-IJKL-202401-A-B";
    private final InformalNotificationDetailMock notificationMock = new InformalNotificationDetailMock();
    private final NotificationDownloadDocumentMock documentMock = new NotificationDownloadDocumentMock();

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InformalNotificationRecipientService informalNotificationRecipientService;

    @Test
    void getReceivedInformalNotification() {
        BffFullInformalNotificationV1 response =
                InformalNotificationReceivedMapper.modelMapper.mapReceivedInformalNotificationDetail(
                        notificationMock.getInformalNotificationMock()
                );
        response.setSenderContacts(new SenderContactInfo()
                .senderId(InformalNotificationDetailMock.SENDER_PA_ID)
                .email("sender@example.com"));
        mockDetail(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(INFORMAL_NOTIFICATION_RECEIVED_PATH)
                        .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(SOURCE_CHANNEL_HEADER, "WEB")
                .header(SOURCE_CHANNEL_DETAILS_HEADER, "details")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BffFullInformalNotificationV1.class)
                .isEqualTo(response);

        verifyDetail();
    }

    @Test
    void getReceivedInformalNotificationError() {
        mockDetail(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(INFORMAL_NOTIFICATION_RECEIVED_PATH)
                        .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .header(SOURCE_CHANNEL_HEADER, "WEB")
                .header(SOURCE_CHANNEL_DETAILS_HEADER, "details")
                .exchange()
                .expectStatus().isNotFound();

        verifyDetail();
    }

    @Test
    void getReceivedInformalNotificationDocument() {
        BffDocumentDownloadMetadataResponse response =
                NotificationDownloadDocumentMapper.modelMapper.mapReceivedAttachmentDownloadResponse(
                        documentMock.getRecipientAttachmentMock()
                );
        Mockito.when(informalNotificationRecipientService.getInformalNotificationDocument(
                Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(INFORMAL_NOTIFICATION_DOCUMENT_PATH).build(IUN, 0))
                .accept(MediaType.APPLICATION_JSON)
                .headers(this::addHeaders)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BffDocumentDownloadMetadataResponse.class)
                .isEqualTo(response);

        Mockito.verify(informalNotificationRecipientService).getInformalNotificationDocument(
                UserMock.PN_UID, CxTypeAuthFleet.PF, UserMock.PN_CX_ID, "WEB", IUN, 0,
                UserMock.PN_CX_GROUPS, "details"
        );
    }

    @Test
    void getReceivedInformalNotificationDocumentError() {
        Mockito.when(informalNotificationRecipientService.getInformalNotificationDocument(
                Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(INFORMAL_NOTIFICATION_DOCUMENT_PATH).build(IUN, 0))
                .accept(MediaType.APPLICATION_JSON)
                .headers(this::addHeaders)
                .exchange()
                .expectStatus().isNotFound();

        Mockito.verify(informalNotificationRecipientService).getInformalNotificationDocument(
                UserMock.PN_UID, CxTypeAuthFleet.PF, UserMock.PN_CX_ID, "WEB", IUN, 0,
                UserMock.PN_CX_GROUPS, "details"
        );
    }

    @Test
    void getReceivedInformalNotificationPaymentAttachment() {
        BffDocumentDownloadMetadataResponse response =
                NotificationDownloadDocumentMapper.modelMapper.mapReceivedAttachmentDownloadResponse(
                        documentMock.getRecipientAttachmentMock()
                );
        Mockito.when(informalNotificationRecipientService.getInformalNotificationDocument(
                Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(),
                Mockito.anyString(), Mockito.anyInt()
        )).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(INFORMAL_NOTIFICATION_PAYMENT_PATH)
                        .queryParam("attachmentIdx", 0)
                        .build(IUN, "PAGOPA"))
                .accept(MediaType.APPLICATION_JSON)
                .headers(this::addHeaders)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BffDocumentDownloadMetadataResponse.class)
                .isEqualTo(response);

        Mockito.verify(informalNotificationRecipientService).getInformalNotificationDocument(
                UserMock.PN_UID, CxTypeAuthFleet.PF, UserMock.PN_CX_ID, "WEB", IUN, "PAGOPA",
                UserMock.PN_CX_GROUPS, "details", 0
        );
    }

    @Test
    void getReceivedInformalNotificationPaymentAttachmentError() {
        Mockito.when(informalNotificationRecipientService.getInformalNotificationDocument(
                Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(),
                Mockito.anyString(), Mockito.anyInt()
        )).thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(INFORMAL_NOTIFICATION_PAYMENT_PATH)
                        .queryParam("attachmentIdx", 0)
                        .build(IUN, "PAGOPA"))
                .accept(MediaType.APPLICATION_JSON)
                .headers(this::addHeaders)
                .exchange()
                .expectStatus().isNotFound();

        Mockito.verify(informalNotificationRecipientService).getInformalNotificationDocument(
                UserMock.PN_UID, CxTypeAuthFleet.PF, UserMock.PN_CX_ID, "WEB", IUN, "PAGOPA",
                UserMock.PN_CX_GROUPS, "details", 0
        );
    }

    private void mockDetail(Mono<BffFullInformalNotificationV1> response) {
        Mockito.when(informalNotificationRecipientService.getInformalNotificationDetail(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(response);
    }

    private void verifyDetail() {
        Mockito.verify(informalNotificationRecipientService).getInformalNotificationDetail(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "WEB",
                IUN,
                UserMock.PN_CX_GROUPS,
                "details"
        );
    }

    private void addHeaders(org.springframework.http.HttpHeaders headers) {
        headers.add(PnBffRestConstants.UID_HEADER, UserMock.PN_UID);
        headers.add(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID);
        headers.add(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue());
        headers.add(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS));
        headers.add(SOURCE_CHANNEL_HEADER, "WEB");
        headers.add(SOURCE_CHANNEL_DETAILS_HEADER, "details");
    }
}
