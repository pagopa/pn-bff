package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationAarQrCodeMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationDownloadDocumentMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationReceivedDetailMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationsReceivedMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.NotificationsRecipientService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import it.pagopa.pn.bff.utils.helpers.MonoMatcher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@WebFluxTest(ReceivedNotificationController.class)
class ReceivedNotificationControllerTest {
    private static final String IUN = "HEUJ-UEPA-HGXT-202401-N-1";
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private NotificationsRecipientService notificationsRecipientService;

    @Test
    void searchReceivedNotifications() {
        BffNotificationsResponse response = NotificationsReceivedMapper.modelMapper.toBffNotificationsResponse(notificationsReceivedMock.getNotificationReceivedPNMock());
        Mockito.when(notificationsRecipientService.searchReceivedNotifications(
                        Mockito.anyString(),
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
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATIONS_RECEIVED_PATH)
                                .queryParam("iunMatch", NotificationsReceivedMock.IUN_MATCH)
                                .queryParam("mandateId", NotificationsReceivedMock.MANDATE_ID)
                                .queryParam("senderId", NotificationsReceivedMock.SENDER_ID)
                                .queryParam("status", NotificationsReceivedMock.STATUS.getValue())
                                .queryParam("startDate", NotificationsReceivedMock.START_DATE)
                                .queryParam("endDate", NotificationsReceivedMock.END_DATE)
                                .queryParam("subjectRegExp", NotificationsReceivedMock.SUBJECT_REG_EXP)
                                .queryParam("size", NotificationsReceivedMock.SIZE)
                                .queryParam("nextPagesKey", NotificationsReceivedMock.NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffNotificationsResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationsRecipientService).searchReceivedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.MANDATE_ID,
                NotificationsReceivedMock.SENDER_ID,
                NotificationsReceivedMock.STATUS,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SUBJECT_REG_EXP,
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void searchReceivedNotificationsError() {
        Mockito.when(notificationsRecipientService.searchReceivedNotifications(
                        Mockito.anyString(),
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
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATIONS_RECEIVED_PATH)
                                .queryParam("iunMatch", NotificationsReceivedMock.IUN_MATCH)
                                .queryParam("mandateId", NotificationsReceivedMock.MANDATE_ID)
                                .queryParam("senderId", NotificationsReceivedMock.SENDER_ID)
                                .queryParam("status", NotificationsReceivedMock.STATUS.getValue())
                                .queryParam("startDate", NotificationsReceivedMock.START_DATE)
                                .queryParam("endDate", NotificationsReceivedMock.END_DATE)
                                .queryParam("subjectRegExp", NotificationsReceivedMock.SUBJECT_REG_EXP)
                                .queryParam("size", NotificationsReceivedMock.SIZE)
                                .queryParam("nextPagesKey", NotificationsReceivedMock.NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsRecipientService).searchReceivedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.MANDATE_ID,
                NotificationsReceivedMock.SENDER_ID,
                NotificationsReceivedMock.STATUS,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SUBJECT_REG_EXP,
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void searchReceivedDelegatedNotifications() {
        BffNotificationsResponse response = NotificationsReceivedMapper.modelMapper.toBffNotificationsResponse(notificationsReceivedMock.getNotificationReceivedPNMock());
        Mockito.when(notificationsRecipientService.searchReceivedDelegatedNotifications(
                        Mockito.anyString(),
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
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATIONS_RECEIVED_DELEGATED_PATH)
                                .queryParam("iunMatch", NotificationsReceivedMock.IUN_MATCH)
                                .queryParam("senderId", NotificationsReceivedMock.SENDER_ID)
                                .queryParam("recipientId", NotificationsReceivedMock.RECIPIENT_ID)
                                .queryParam("group", NotificationsReceivedMock.GROUP)
                                .queryParam("status", NotificationsReceivedMock.STATUS.getValue())
                                .queryParam("startDate", NotificationsReceivedMock.START_DATE)
                                .queryParam("endDate", NotificationsReceivedMock.END_DATE)
                                .queryParam("size", NotificationsReceivedMock.SIZE)
                                .queryParam("nextPagesKey", NotificationsReceivedMock.NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffNotificationsResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationsRecipientService).searchReceivedDelegatedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.SENDER_ID,
                NotificationsReceivedMock.RECIPIENT_ID,
                NotificationsReceivedMock.GROUP,
                NotificationsReceivedMock.STATUS,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void searchReceivedDelegatedNotificationsError() {
        Mockito.when(notificationsRecipientService.searchReceivedDelegatedNotifications(
                        Mockito.anyString(),
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
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATIONS_RECEIVED_DELEGATED_PATH)
                                .queryParam("iunMatch", NotificationsReceivedMock.IUN_MATCH)
                                .queryParam("senderId", NotificationsReceivedMock.SENDER_ID)
                                .queryParam("recipientId", NotificationsReceivedMock.RECIPIENT_ID)
                                .queryParam("group", NotificationsReceivedMock.GROUP)
                                .queryParam("status", NotificationsReceivedMock.STATUS.getValue())
                                .queryParam("startDate", NotificationsReceivedMock.START_DATE)
                                .queryParam("endDate", NotificationsReceivedMock.END_DATE)
                                .queryParam("size", NotificationsReceivedMock.SIZE)
                                .queryParam("nextPagesKey", NotificationsReceivedMock.NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsRecipientService).searchReceivedDelegatedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.SENDER_ID,
                NotificationsReceivedMock.RECIPIENT_ID,
                NotificationsReceivedMock.GROUP,
                NotificationsReceivedMock.STATUS,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void getReceivedNotification() {
        BffFullNotificationV1 response = NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(notificationDetailRecipientMock.getNotificationMultiRecipientMock());
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
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


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
        BffDocumentDownloadMetadataResponse response = NotificationDownloadDocumentMapper.modelMapper.mapDocumentDownloadResponse(notificationDownloadDocumentMock.getDocumentMock());
        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentId", "aar-id")
                                .build(IUN, BffDocumentType.AAR))
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
                BffDocumentType.AAR,
                null,
                "aar-id",
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentAARError() {
        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentId", "aar-id")
                                .build(IUN, BffDocumentType.AAR))
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
                BffDocumentType.AAR,
                null,
                "aar-id",
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentLegalFact() {
        BffDocumentDownloadMetadataResponse response = NotificationDownloadDocumentMapper.modelMapper.mapLegalFactDownloadResponse(notificationDownloadDocumentMock.getLegalFactMock());
        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentId", "legal-fact-id")
                                .build(IUN, BffDocumentType.LEGAL_FACT))
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
                BffDocumentType.LEGAL_FACT,
                null,
                "legal-fact-id",
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentLegalFactError() {
        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentId", "legal-fact-id")
                                .build(IUN, BffDocumentType.LEGAL_FACT))
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
                BffDocumentType.LEGAL_FACT,
                null,
                "legal-fact-id",
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentAttachment() {
        BffDocumentDownloadMetadataResponse response = NotificationDownloadDocumentMapper.modelMapper.mapReceivedAttachmentDownloadResponse(notificationDownloadDocumentMock.getRecipientAttachmentMock());
        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentIdx", 0)
                                .build(IUN, BffDocumentType.ATTACHMENT))
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
                BffDocumentType.ATTACHMENT,
                0,
                null,
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationDocumentAttachmentError() {
        Mockito.when(notificationsRecipientService.getReceivedNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class)
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_DOCUMENT_PATH)
                                .queryParam("documentIdx", 0)
                                .build(IUN, BffDocumentType.ATTACHMENT))
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
                BffDocumentType.ATTACHMENT,
                0,
                null,
                UserMock.PN_CX_GROUPS,
                null
        );
    }

    @Test
    void getReceivedNotificationPayment() {
        BffDocumentDownloadMetadataResponse response = NotificationDownloadDocumentMapper.modelMapper.mapReceivedAttachmentDownloadResponse(notificationDownloadDocumentMock.getRecipientAttachmentMock());
        Mockito.when(notificationsRecipientService.getReceivedNotificationPayment(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class),
                        Mockito.anyInt()
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_PAYMENT_PATH)
                                .queryParam("attachmentIdx", 0)
                                .build(IUN, "PAGOPA"))
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

        Mockito.verify(notificationsRecipientService).getReceivedNotificationPayment(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                "PAGOPA",
                UserMock.PN_CX_GROUPS,
                null,
                0
        );
    }

    @Test
    void getReceivedNotificationPaymentError() {
        Mockito.when(notificationsRecipientService.getReceivedNotificationPayment(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList(),
                        Mockito.nullable(UUID.class),
                        Mockito.anyInt()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "BAD_REQUEST")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_RECEIVED_PAYMENT_PATH)
                                .queryParam("attachmentIdx", 0)
                                .build(IUN, "PAGOPA"))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsRecipientService).getReceivedNotificationPayment(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                IUN,
                "PAGOPA",
                UserMock.PN_CX_GROUPS,
                null,
                0
        );
    }

    @Test
    void checkAarQrCode() {
        BffCheckAarResponse response = NotificationAarQrCodeMapper.modelMapper.toBffResponseCheckAarMandateDto(notificationsReceivedMock.getResponseCheckAarMandateDtoPNMock());
        Mockito.when(notificationsRecipientService.checkAarQrCode(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.just(response)); // E questa riga

        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_AAR_QR_CODE_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .bodyValue(notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffCheckAarResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationsRecipientService).checkAarQrCode(
                eq(UserMock.PN_UID),
                eq(CxTypeAuthFleet.PF),
                eq(UserMock.PN_CX_ID),
                argThat(new MonoMatcher<>(Mono.just(notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock()))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }

    @Test
    void checkAarQrCodeError() {
        Mockito.when(notificationsRecipientService.checkAarQrCode(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "BAD_REQUEST")));

        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_AAR_QR_CODE_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .bodyValue(notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock())
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsRecipientService).checkAarQrCode(
                eq(UserMock.PN_UID),
                eq(CxTypeAuthFleet.PF),
                eq(UserMock.PN_CX_ID),
                argThat(new MonoMatcher<>(Mono.just(notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock()))),
                eq(UserMock.PN_CX_GROUPS)
        );
    }
}