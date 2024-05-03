package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationDetailMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationDownloadDocumentMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationsSentMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.NotificationsSentMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
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

class NotificationPaServiceTest {

    private static NotificationsPAService notificationsPAService;
    private static PnDeliveryClientPAImpl pnDeliveryClientPA;
    private static PnDeliveryPushClientImpl pnDeliveryPushClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();
    private final NotificationsSentMock notificationsSentMock = new NotificationsSentMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();

    @BeforeAll
    public static void setup() {
        pnDeliveryClientPA = mock(PnDeliveryClientPAImpl.class);
        pnDeliveryPushClient = mock(PnDeliveryPushClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        notificationsPAService = new NotificationsPAService(pnDeliveryClientPA, pnDeliveryPushClient, pnBffExceptionUtility);
    }

    @Test
    void searchSentNotifications() {
        when(pnDeliveryClientPA.searchSentNotifications(
                Mockito.any(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationsSentMock.getNotificationSentPNMock()));

        Mono<BffNotificationsResponse> result = notificationsPAService.searchSentNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                NotificationsSentMock.IUN_MATCH,
                NotificationsSentMock.SENDER_ID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus.ACCEPTED,
                NotificationsSentMock.SUBJECT_REG_EXP,
                OffsetDateTime.parse(NotificationsSentMock.START_DATE),
                OffsetDateTime.parse(NotificationsSentMock.END_DATE),
                NotificationsSentMock.SIZE,
                NotificationsSentMock.NEXT_PAGES_KEY
        );

        StepVerifier.create(result)
                .expectNext(NotificationsSentMapper.modelMapper.toBffNotificationsResponse(notificationsSentMock.getNotificationSentPNMock()))
                .verifyComplete();
    }

    @Test
    void searchSentNotificationError() {
        when(pnDeliveryClientPA.searchSentNotifications(
                Mockito.any(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyList(),
                Mockito.anyString(),
                Mockito.any(NotificationStatus.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffNotificationsResponse> result = notificationsPAService.searchSentNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                NotificationsSentMock.IUN_MATCH,
                NotificationsSentMock.SENDER_ID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus.ACCEPTED,
                NotificationsSentMock.SUBJECT_REG_EXP,
                OffsetDateTime.parse(NotificationsSentMock.START_DATE),
                OffsetDateTime.parse(NotificationsSentMock.END_DATE),
                NotificationsSentMock.SIZE,
                NotificationsSentMock.NEXT_PAGES_KEY
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDetail() {
        when(pnDeliveryClientPA.getSentNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.just(notificationDetailPaMock.getNotificationMultiRecipientMock()));

        Mono<BffFullNotificationV1> result = notificationsPAService.getSentNotificationDetail(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(NotificationDetailMapper.modelMapper.mapSentNotificationDetail(notificationDetailPaMock.getNotificationMultiRecipientMock()))
                .verifyComplete();
    }

    @Test
    void getNotificationDetailError() {
        when(pnDeliveryClientPA.getSentNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffFullNotificationV1> result = notificationsPAService.getSentNotificationDetail(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                UserMock.PN_CX_GROUPS
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

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.AAR,
                null,
                UserMock.PN_CX_GROUPS
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

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.AAR,
                null,
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDocumentAARNoDocumentId() {
        DocumentId documentId = new DocumentId();

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.AAR,
                null,
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400
                        && Objects.equals(((PnBffException) throwable).getProblem().getType(), "GENERIC_ERROR")
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The AAR id is missed")
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

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.LEGAL_FACT,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.LegalFactCategory.ANALOG_DELIVERY,
                UserMock.PN_CX_GROUPS
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

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.LEGAL_FACT,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.LegalFactCategory.ANALOG_DELIVERY,
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDocumentLegalFactNoDocumentId() {
        DocumentId documentId = new DocumentId();

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.LEGAL_FACT,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.LegalFactCategory.ANALOG_DELIVERY,
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400
                        && Objects.equals(((PnBffException) throwable).getProblem().getType(), "GENERIC_ERROR")
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The legal fact id is missed")
                )
                .verify();
    }

    @Test
    void getNotificationDocumentLegalFactNoLegalFactCategory() {
        DocumentId documentId = new DocumentId();
        documentId.setLegalFactId("legal-fact-id");

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.LEGAL_FACT,
                null,
                UserMock.PN_CX_GROUPS
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
        when(pnDeliveryClientPA.getSentNotificationDocument(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyList()
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getPaAttachmentMock()));

        DocumentId documentId = new DocumentId();
        documentId.setAttachmentIdx(0);

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.ATTACHMENT,
                null,
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(NotificationDownloadDocumentMapper.modelMapper.mapSentAttachmentDownloadResponse(notificationDownloadDocumentMock.getPaAttachmentMock()))
                .verifyComplete();
    }

    @Test
    void getNotificationDocumentAttachmentError() {
        when(pnDeliveryClientPA.getSentNotificationDocument(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        DocumentId documentId = new DocumentId();
        documentId.setAttachmentIdx(0);

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.ATTACHMENT,
                null,
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDocumentAttachmentNoDocumentId() {
        DocumentId documentId = new DocumentId();

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "IUN",
                documentId,
                BffDocumentType.ATTACHMENT,
                null,
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400
                        && Objects.equals(((PnBffException) throwable).getProblem().getType(), "GENERIC_ERROR")
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The attachment idx is missed")
                )
                .verify();
    }
}