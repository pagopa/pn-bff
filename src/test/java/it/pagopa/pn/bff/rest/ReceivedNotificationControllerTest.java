package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationDetailMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationDownloadDocumentMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.NotificationsRecipientService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@WebFluxTest(ReceivedNotificationController.class)
class ReceivedNotificationControllerTest {
    private static final String IUN = "HEUJ-UEPA-HGXT-202401-N-1";
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private NotificationsRecipientService notificationsRecipientService;
    @SpyBean
    private ReceivedNotificationController receivedNotificationController;

    @Test
    void getReceivedNotification() {
        BffFullNotificationV1 response = NotificationDetailMapper.modelMapper.mapReceivedNotificationDetail(notificationDetailRecipientMock.getNotificationMultiRecipientMock());
        Mockito.when(notificationsRecipientService.getNotificationDetail(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.any()
                ))
                .thenReturn(Mono.just(response));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffFullNotificationV1.class)
                .isEqualTo(response);

        Mockito.verify(notificationsRecipientService).getNotificationDetail(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationError() {
        Mockito.when(notificationsRecipientService.getNotificationDetail(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.any()
                ))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsRecipientService).getNotificationDetail(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentAAR() {
        DocumentId documentId = new DocumentId();
        documentId.setAarId("aar-id");

        BffDocumentDownloadMetadataResponse response = NotificationDownloadDocumentMapper.modelMapper.mapDocumentDownloadResponse(notificationDownloadDocumentMock.getDocumentMock());
        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(DocumentId.class),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.AAR)
                                .queryParam("aarId", documentId.getAarId())
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffDocumentDownloadMetadataResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationsRecipientService).getReceivedNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                documentId,
                BffDocumentType.AAR,
                null,
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentAARError() {
        DocumentId documentId = new DocumentId();
        documentId.setAarId("aar-id");

        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(DocumentId.class),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.AAR)
                                .queryParam("aarId", documentId.getAarId())
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsRecipientService).getReceivedNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                documentId,
                BffDocumentType.AAR,
                null,
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentLegalFact() {
        DocumentId documentId = new DocumentId();
        documentId.setLegalFactId("legal-fact-id");

        BffDocumentDownloadMetadataResponse response = NotificationDownloadDocumentMapper.modelMapper.mapLegalFactDownloadResponse(notificationDownloadDocumentMock.getLegalFactMock());
        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(DocumentId.class),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.LEGAL_FACT)
                                .queryParam("legalFactId", documentId.getLegalFactId())
                                .queryParam("legalFactCategory", LegalFactCategory.ANALOG_DELIVERY)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffDocumentDownloadMetadataResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationsRecipientService).getReceivedNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                documentId,
                BffDocumentType.LEGAL_FACT,
                LegalFactCategory.ANALOG_DELIVERY,
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentLegalFactError() {
        DocumentId documentId = new DocumentId();
        documentId.setLegalFactId("legal-fact-id");

        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(DocumentId.class),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.LEGAL_FACT)
                                .queryParam("legalFactId", documentId.getLegalFactId())
                                .queryParam("legalFactCategory", LegalFactCategory.ANALOG_DELIVERY)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsRecipientService).getReceivedNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                documentId,
                BffDocumentType.LEGAL_FACT,
                LegalFactCategory.ANALOG_DELIVERY,
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentAttachment() {
        DocumentId documentId = new DocumentId();
        documentId.setAttachmentIdx(0);

        BffDocumentDownloadMetadataResponse response = NotificationDownloadDocumentMapper.modelMapper.mapReceivedAttachmentDownloadResponse(notificationDownloadDocumentMock.getRecipientAttachmentMock());
        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(DocumentId.class),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.ATTACHMENT)
                                .queryParam("attachmentIdx", documentId.getAttachmentIdx())
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffDocumentDownloadMetadataResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationsRecipientService).getReceivedNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                documentId,
                BffDocumentType.ATTACHMENT,
                null,
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentAttachmentError() {
        DocumentId documentId = new DocumentId();
        documentId.setAttachmentIdx(0);

        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(DocumentId.class),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.ATTACHMENT)
                                .queryParam("attachmentIdx", documentId.getAttachmentIdx())
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsRecipientService).getReceivedNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                documentId,
                BffDocumentType.ATTACHMENT,
                null,
                UserMock.PN_CX_GROUPS,
                null
        );
    }
}