package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationCancellationMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationDetailMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationDownloadDocumentMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationsSentMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.NotificationsSentMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.NotificationsPAService;
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
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Slf4j
@WebFluxTest(SentNotificationController.class)
class SentNotificationControllerTest {
    private static final String IUN = "HEUJ-UEPA-HGXT-202401-N-1";
    private final NotificationsSentMock notificationsSentMock = new NotificationsSentMock();
    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();

    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private NotificationsPAService notificationsPAService;
    @SpyBean
    private SentNotificationController sentNotificationController;

    @Test
    void searchSentNotifications() {
        BffNotificationsResponse response = NotificationsSentMapper.modelMapper.toBffNotificationsResponse(notificationsSentMock.getNotificationSentPNMock());
        Mockito.when(notificationsPAService.searchSentNotifications(
                Mockito.any(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATIONS_SENT_PATH)
                                .queryParam("startDate", NotificationsSentMock.START_DATE)
                                .queryParam("endDate", NotificationsSentMock.END_DATE)
                                .queryParam("size", NotificationsSentMock.SIZE)
                                .queryParam("iunMatch", NotificationsSentMock.IUN_MATCH)
                                .queryParam("recipientId", NotificationsSentMock.RECIPIENT_ID)
                                .queryParam("status", NotificationsSentMock.STATUS.getValue())
                                .queryParam("subjectRegExp", NotificationsSentMock.SUBJECT_REG_EXP)
                                .queryParam("nextPagesKey", NotificationsSentMock.NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffNotificationsResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationsPAService).searchSentNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                NotificationsSentMock.IUN_MATCH,
                NotificationsSentMock.RECIPIENT_ID,
                NotificationsSentMock.STATUS,
                NotificationsSentMock.SUBJECT_REG_EXP,
                OffsetDateTime.parse(NotificationsSentMock.START_DATE),
                OffsetDateTime.parse(NotificationsSentMock.END_DATE),
                NotificationsSentMock.SIZE,
                NotificationsSentMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void searchSentNotificationsError() {
        Mockito.when(notificationsPAService.searchSentNotifications(
                Mockito.any(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "BAD_REQUEST")));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATIONS_SENT_PATH)
                                .queryParam("startDate", NotificationsSentMock.START_DATE)
                                .queryParam("endDate", NotificationsSentMock.END_DATE)
                                .queryParam("size", NotificationsSentMock.SIZE)
                                .queryParam("iunMatch", NotificationsSentMock.IUN_MATCH)
                                .queryParam("recipientId", NotificationsSentMock.RECIPIENT_ID)
                                .queryParam("status", NotificationsSentMock.STATUS.getValue())
                                .queryParam("subjectRegExp", NotificationsSentMock.SUBJECT_REG_EXP)
                                .queryParam("nextPagesKey", NotificationsSentMock.NEXT_PAGES_KEY)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsPAService).searchSentNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                NotificationsSentMock.IUN_MATCH,
                NotificationsSentMock.RECIPIENT_ID,
                NotificationsSentMock.STATUS,
                NotificationsSentMock.SUBJECT_REG_EXP,
                OffsetDateTime.parse(NotificationsSentMock.START_DATE),
                OffsetDateTime.parse(NotificationsSentMock.END_DATE),
                NotificationsSentMock.SIZE,
                NotificationsSentMock.NEXT_PAGES_KEY
        );
    }

    @Test
    void getSentNotification() {
        BffFullNotificationV1 response = NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDetailPaMock.getNotificationMultiRecipientMock());
        Mockito.when(notificationsPAService.getSentNotificationDetail(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.just(response));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SENT_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffFullNotificationV1.class)
                .isEqualTo(response);

        Mockito.verify(notificationsPAService).getSentNotificationDetail(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void getSentNotificationError() {
        Mockito.when(notificationsPAService.getSentNotificationDetail(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "BAD_REQUEST")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SENT_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsPAService).getSentNotificationDetail(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void getSentNotificationDocumentAAR() {
        BffDocumentDownloadMetadataResponse response = NotificationDownloadDocumentMapper.modelMapper.mapDocumentDownloadResponse(notificationDownloadDocumentMock.getDocumentMock());
        Mockito.when(notificationsPAService.getSentNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SENT_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.AAR)
                                .queryParam("documentId", "aar-id")
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffDocumentDownloadMetadataResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationsPAService).getSentNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                BffDocumentType.AAR,
                null,
                "aar-id",
                null,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void getSentNotificationDocumentAARError() {

        Mockito.when(notificationsPAService.getSentNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "BAD_REQUEST")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SENT_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.AAR)
                                .queryParam("documentId", "aar-id")
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsPAService).getSentNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                BffDocumentType.AAR,
                null,
                "aar-id",
                null,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void getSentNotificationDocumentLegalFact() {
        BffDocumentDownloadMetadataResponse response = NotificationDownloadDocumentMapper.modelMapper.mapLegalFactDownloadResponse(notificationDownloadDocumentMock.getLegalFactMock());
        Mockito.when(notificationsPAService.getSentNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SENT_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.LEGAL_FACT)
                                .queryParam("documentId", "legal-fact-id")
                                .queryParam("documentCategory", LegalFactCategory.ANALOG_DELIVERY)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffDocumentDownloadMetadataResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationsPAService).getSentNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                BffDocumentType.LEGAL_FACT,
                null,
                "legal-fact-id",
                LegalFactCategory.ANALOG_DELIVERY,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void getSentNotificationDocumentLegalFactError() {
        Mockito.when(notificationsPAService.getSentNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "BAD_REQUEST")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SENT_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.LEGAL_FACT)
                                .queryParam("documentId", "legal-fact-id")
                                .queryParam("documentCategory", LegalFactCategory.ANALOG_DELIVERY)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsPAService).getSentNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                BffDocumentType.LEGAL_FACT,
                null,
                "legal-fact-id",
                LegalFactCategory.ANALOG_DELIVERY,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void getSentNotificationDocumentAttachment() {

        BffDocumentDownloadMetadataResponse response = NotificationDownloadDocumentMapper.modelMapper.mapSentAttachmentDownloadResponse(notificationDownloadDocumentMock.getPaAttachmentMock());
        Mockito.when(notificationsPAService.getSentNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SENT_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.ATTACHMENT)
                                .queryParam("documentIdx", 0)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffDocumentDownloadMetadataResponse.class)
                .isEqualTo(response);

        Mockito.verify(notificationsPAService).getSentNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                BffDocumentType.ATTACHMENT,
                0,
                null,
                null,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void getSentNotificationDocumentAttachmentError() {
        Mockito.when(notificationsPAService.getSentNotificationDocument(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(BffDocumentType.class),
                        Mockito.nullable(Integer.class),
                        Mockito.nullable(String.class),
                        Mockito.nullable(LegalFactCategory.class),
                        Mockito.anyList()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "BAD_REQUEST")));


        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_SENT_DOCUMENT_PATH)
                                .queryParam("documentType", BffDocumentType.ATTACHMENT)
                                .queryParam("documentIdx", 0)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsPAService).getSentNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                BffDocumentType.ATTACHMENT,
                0,
                null,
                null,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void notificationCancellation(){
        BffRequestStatus response = NotificationCancellationMapper.modelMapper.mapNotificationCancellation(notificationsSentMock.notificationCancellationPNMock());
        Mockito.when(notificationsPAService.notificationCancellation(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.just(response));

        webTestClient.put()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_CANCEL_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffRequestStatus.class)
                .isEqualTo(response);

        Mockito.verify(notificationsPAService).notificationCancellation(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS
        );
    }

    @Test
    void notificationCancellationError(){
        Mockito.when(notificationsPAService.notificationCancellation(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "BAD_REQUEST")));

        webTestClient.put()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.NOTIFICATION_CANCEL_PATH)
                                .build(IUN))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_GROUPS_HEADER, String.join(",", UserMock.PN_CX_GROUPS))
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(notificationsPAService).notificationCancellation(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                IUN,
                UserMock.PN_CX_GROUPS
        );
    }
}