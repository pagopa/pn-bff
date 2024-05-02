package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactCategory;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffDocumentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.DocumentId;
import it.pagopa.pn.bff.mappers.notifications.NotificationDetailMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationDownloadDocumentMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationPaServiceTest {

    private static NotificationsPAService notificationsPAService;
    private static PnDeliveryClientPAImpl pnDeliveryClientPA;
    private static PnDeliveryPushClientImpl pnDeliveryPushClient;
    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();

    @BeforeAll
    public static void setup() {
        pnDeliveryClientPA = mock(PnDeliveryClientPAImpl.class);
        pnDeliveryPushClient = mock(PnDeliveryPushClientImpl.class);
        notificationsPAService = new NotificationsPAService(pnDeliveryClientPA, pnDeliveryPushClient);
    }

    @Test
    void testGetNotificationDetail() {
        when(pnDeliveryClientPA.getSentNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet.class),
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
    void testGetNotificationDetailError() {
        when(pnDeliveryClientPA.getSentNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet.class),
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
    void testGetNotificationDocumentAAR() {
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
    void testGetNotificationDocumentAARError() {
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
    void testGetNotificationDocumentAARNoDocumentId() {
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
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The document id is missed")
                )
                .verify();
    }

    @Test
    void testGetNotificationDocumentLegalFact() {
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
    void testGetNotificationDocumentLegalFactError() {
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
    void testGetNotificationDocumentLegalFactNoDocumentId() {
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
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The document id is missed")
                )
                .verify();
    }

    @Test
    void testGetNotificationDocumentLegalFactNoLegalFactCategory() {
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
    void testGetNotificationDocumentAttachment() {
        when(pnDeliveryClientPA.getSentNotificationDocument(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet.class),
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
    void testGetNotificationDocumentAttachmentError() {
        when(pnDeliveryClientPA.getSentNotificationDocument(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet.class),
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
    void testGetNotificationDocumentAttachmentNoDocumentId() {
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
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The document id is missed")
                )
                .verify();
    }
}