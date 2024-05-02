package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationDetailMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationDownloadDocumentMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationsReceivedMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationRecipientServiceTest {

    private static NotificationsRecipientService notificationsRecipientService;
    private static PnDeliveryClientRecipientImpl pnDeliveryClientRecipient;
    private static PnDeliveryPushClientImpl pnDeliveryPushClient;
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();

    @BeforeAll
    public static void setup() {
        pnDeliveryClientRecipient = mock(PnDeliveryClientRecipientImpl.class);
        pnDeliveryPushClient = mock(PnDeliveryPushClientImpl.class);
        notificationsRecipientService = new NotificationsRecipientService(pnDeliveryClientRecipient, pnDeliveryPushClient);
    }

    @Test
    void searchReceivedNotifications() {
        when(pnDeliveryClientRecipient.searchReceivedNotifications(
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
        )).thenReturn(Mono.just(notificationsReceivedMock.getNotificationReceivedPNMock()));

        Mono<BffNotificationsResponse> result = notificationsRecipientService.searchReceivedNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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

        StepVerifier.create(result)
                .expectNext(NotificationsReceivedMapper.modelMapper.toBffNotificationsResponse(notificationsReceivedMock.getNotificationReceivedPNMock()))
                .verifyComplete();
    }

    @Test
    void searchReceivedNotificationsError() {
        when(pnDeliveryClientRecipient.searchReceivedNotifications(
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

        Mono<BffNotificationsResponse> result = notificationsRecipientService.searchReceivedNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void searchReceivedDelegatedNotifications() {
        when(pnDeliveryClientRecipient.searchReceivedDelegatedNotifications(
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
        )).thenReturn(Mono.just(notificationsReceivedMock.getNotificationReceivedPNMock()));

        Mono<BffNotificationsResponse> result = notificationsRecipientService.searchReceivedDelegatedNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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

        StepVerifier.create(result)
                .expectNext(NotificationsReceivedMapper.modelMapper.toBffNotificationsResponse(notificationsReceivedMock.getNotificationReceivedPNMock()))
                .verifyComplete();
    }

    @Test
    void searchReceivedDelegatedNotificationsError() {
        when(pnDeliveryClientRecipient.searchReceivedDelegatedNotifications(
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

        Mono<BffNotificationsResponse> result = notificationsRecipientService.searchReceivedDelegatedNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PG,
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

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDetail() {
        when(pnDeliveryClientRecipient.getReceivedNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationDetailRecipientMock.getNotificationMultiRecipientMock()));

        Mono<BffFullNotificationV1> result = notificationsRecipientService.getNotificationDetail(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                UserMock.PN_CX_GROUPS,
                "MANDATE_ID"
        );

        StepVerifier.create(result)
                .expectNext(NotificationDetailMapper.modelMapper.mapReceivedNotificationDetail(notificationDetailRecipientMock.getNotificationMultiRecipientMock()))
                .verifyComplete();
    }

    @Test
    void getNotificationDetailError() {
        when(pnDeliveryClientRecipient.getReceivedNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffFullNotificationV1> result = notificationsRecipientService.getNotificationDetail(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                UserMock.PN_CX_GROUPS,
                "MANDATE_ID"
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDocumentAAR() {
        when(pnDeliveryPushClient.getDocumentsWeb(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(DocumentCategory.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(UUID.class)
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getDocumentMock()));

        DocumentId documentId = new DocumentId();
        documentId.setAarId("aar-id");

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.AAR,
                null,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        );

        StepVerifier.create(result)
                .expectNext(NotificationDownloadDocumentMapper.modelMapper.mapDocumentDownloadResponse(notificationDownloadDocumentMock.getDocumentMock()))
                .verifyComplete();
    }

    @Test
    void getNotificationDocumentAARError() {
        when(pnDeliveryPushClient.getDocumentsWeb(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(DocumentCategory.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(UUID.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        DocumentId documentId = new DocumentId();
        documentId.setAarId("aar-id");

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.AAR,
                null,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDocumentAARNoDocumentId() {
        DocumentId documentId = new DocumentId();

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.AAR,
                null,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400
                        && Objects.equals(((PnBffException) throwable).getProblem().getType(), "GENERIC_ERROR")
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The document id is missed")
                )
                .verify();
    }

    @Test
    void getNotificationDocumentLegalFact() {
        when(pnDeliveryPushClient.getLegalFact(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(LegalFactCategory.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(UUID.class)
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getLegalFactMock()));

        DocumentId documentId = new DocumentId();
        documentId.setLegalFactId("legal-fact-id");

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.LEGAL_FACT,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.LegalFactCategory.ANALOG_DELIVERY,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        );

        StepVerifier.create(result)
                .expectNext(NotificationDownloadDocumentMapper.modelMapper.mapLegalFactDownloadResponse(notificationDownloadDocumentMock.getLegalFactMock()))
                .verifyComplete();
    }

    @Test
    void getNotificationDocumentLegalFactError() {
        when(pnDeliveryPushClient.getLegalFact(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(LegalFactCategory.class),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(UUID.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        DocumentId documentId = new DocumentId();
        documentId.setLegalFactId("legal-fact-id");

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.LEGAL_FACT,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.LegalFactCategory.ANALOG_DELIVERY,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDocumentLegalFactNoDocumentId() {
        DocumentId documentId = new DocumentId();

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.LEGAL_FACT,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.LegalFactCategory.ANALOG_DELIVERY,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400
                        && Objects.equals(((PnBffException) throwable).getProblem().getType(), "GENERIC_ERROR")
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The document id is missed")
                )
                .verify();
    }

    @Test
    void getNotificationDocumentLegalFactNoLegalFactCategory() {
        DocumentId documentId = new DocumentId();
        documentId.setLegalFactId("legal-fact-id");

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.LEGAL_FACT,
                null,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400
                        && Objects.equals(((PnBffException) throwable).getProblem().getType(), "GENERIC_ERROR")
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The legal fact category is missed")
                )
                .verify();
    }

    @Test
    void getNotificationDocumentAttachment() {
        when(pnDeliveryClientRecipient.getReceivedNotificationDocument(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyList(),
                Mockito.any(UUID.class)
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getRecipientAttachmentMock()));

        DocumentId documentId = new DocumentId();
        documentId.setAttachmentIdx(0);

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.ATTACHMENT,
                null,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        );

        StepVerifier.create(result)
                .expectNext(NotificationDownloadDocumentMapper.modelMapper.mapSentAttachmentDownloadResponse(notificationDownloadDocumentMock.getPaAttachmentMock()))
                .verifyComplete();
    }

    @Test
    void getNotificationDocumentAttachmentError() {
        when(pnDeliveryClientRecipient.getReceivedNotificationDocument(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyList(),
                Mockito.any(UUID.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        DocumentId documentId = new DocumentId();
        documentId.setAttachmentIdx(0);

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.ATTACHMENT,
                null,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDocumentAttachmentNoDocumentId() {
        DocumentId documentId = new DocumentId();

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.ATTACHMENT,
                null,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400
                        && Objects.equals(((PnBffException) throwable).getProblem().getType(), "GENERIC_ERROR")
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The document id is missed")
                )
                .verify();
    }
}