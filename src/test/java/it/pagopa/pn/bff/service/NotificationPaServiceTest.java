package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.PreLoadResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.*;
import it.pagopa.pn.bff.mocks.*;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientPAImpl;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;
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
    private final NewSentNotificationMock newSentNotificationMock = new NewSentNotificationMock();
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                NotificationsSentMock.IUN_MATCH,
                NotificationsSentMock.SENDER_ID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.NotificationStatus.ACCEPTED,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                NotificationsSentMock.IUN_MATCH,
                NotificationsSentMock.SENDER_ID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.NotificationStatus.ACCEPTED,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(NotificationSentDetailMapper.modelMapper.mapSentNotificationDetail(notificationDetailPaMock.getNotificationMultiRecipientMock()))
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
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


        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.AAR,
                null,
                "aar-id",
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

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.AAR,
                null,
                "aar-id",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDocumentAARNoDocumentId() {

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.AAR,
                null,
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
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(UUID.class)
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getLegalFactMock()));

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.LEGAL_FACT,
                null,
                "legal-fact-id",
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
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(UUID.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.LEGAL_FACT,
                null,
                "legal-fact-id",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getNotificationDocumentLegalFactNoDocumentId() {

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.LEGAL_FACT,
                null,
                null,
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
    void getNotificationDocumentAttachment() {
        when(pnDeliveryClientPA.getSentNotificationDocument(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyList()
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getPaAttachmentMock()));

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.ATTACHMENT,
                0,
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

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.ATTACHMENT,
                0,
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
        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.ATTACHMENT,
                null,
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

    @Test
    void getNotificationPayment() {
        when(pnDeliveryClientPA.getSentNotificationPayment(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt()
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getPaAttachmentMock()));

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationPayment(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                0,
                "PAGOPA",
                UserMock.PN_CX_GROUPS,
                0
        );

        StepVerifier.create(result)
                .expectNext(NotificationDownloadDocumentMapper.modelMapper.mapSentAttachmentDownloadResponse(notificationDownloadDocumentMock.getPaAttachmentMock()))
                .verifyComplete();
    }

    @Test
    void getNotificationPaymentError() {
        when(pnDeliveryClientPA.getSentNotificationPayment(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyInt()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsPAService.getSentNotificationPayment(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                0,
                "PAGOPA",
                UserMock.PN_CX_GROUPS,
                0
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void notificationCancellation() {
        when(pnDeliveryPushClient.notificationCancellation(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.just(notificationsSentMock.notificationCancellationPNMock()));

        Mono<BffRequestStatus> result = notificationsPAService.notificationCancellation(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                "IUN",
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(NotificationCancellationMapper.modelMapper.mapNotificationCancellation(notificationsSentMock.notificationCancellationPNMock()))
                .verifyComplete();
    }

    @Test
    void notificationCancellationError() {
        when(pnDeliveryPushClient.notificationCancellation(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffRequestStatus> result = notificationsPAService.notificationCancellation(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
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
    void newSentNotification() {
        when(pnDeliveryClientPA.newSentNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NewNotificationRequestV24.class),
                Mockito.anyList()
        )).thenReturn(Mono.just(newSentNotificationMock.getNewSentNotificationResponse()));

        Mono<BffNewNotificationResponse> result = notificationsPAService.newSentNotification(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                Mono.just(newSentNotificationMock.getBffNewSentNotificationRequest()),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(NewSentNotificationMapper.modelMapper.mapResponse(newSentNotificationMock.getNewSentNotificationResponse()))
                .verifyComplete();
    }

    @Test
    void newSentNotificationError() {
        when(pnDeliveryClientPA.newSentNotification(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NewNotificationRequestV24.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffNewNotificationResponse> result = notificationsPAService.newSentNotification(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                Mono.just(newSentNotificationMock.getBffNewSentNotificationRequest()),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void preSignedUpload() {
        List<PreLoadResponse> response = newSentNotificationMock.getPreloadResponseMock();
        List<BffPreLoadResponse> bffResponse = response
                .stream()
                .map(NotificationSentPreloadDocumentsMapper.modelMapper::mapResponse)
                .toList();
        when(pnDeliveryClientPA.preSignedUpload(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Flux.fromIterable(response));

        Flux<BffPreLoadResponse> result = notificationsPAService.preSignedUpload(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                Flux.fromIterable(newSentNotificationMock.getBffPreloadRequestMock())
        );

        StepVerifier.create(result)
                .expectNextSequence(bffResponse)
                .verifyComplete();
    }

    @Test
    void preSignedUploadError() {
        when(pnDeliveryClientPA.preSignedUpload(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Flux<BffPreLoadResponse> result = notificationsPAService.preSignedUpload(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PA,
                UserMock.PN_CX_ID,
                Flux.fromIterable(newSentNotificationMock.getBffPreloadRequestMock())
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}