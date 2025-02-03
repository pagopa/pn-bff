package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.notifications.NotificationAarQrCodeMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationDownloadDocumentMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationReceivedDetailMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationsReceivedMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import it.pagopa.pn.bff.pnclient.deliverypush.PnDeliveryPushClientImpl;
import it.pagopa.pn.bff.pnclient.emd.PnEmdClientImpl;
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

class NotificationRecipientServiceTest {

    private static NotificationsRecipientService notificationsRecipientService;
    private static PnDeliveryClientRecipientImpl pnDeliveryClientRecipient;
    private static PnDeliveryPushClientImpl pnDeliveryPushClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    private static PnEmdClientImpl pnEmdClient;
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();

    @BeforeAll
    public static void setup() {
        pnDeliveryClientRecipient = mock(PnDeliveryClientRecipientImpl.class);
        pnDeliveryPushClient = mock(PnDeliveryPushClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        pnEmdClient = mock(PnEmdClientImpl.class);
        notificationsRecipientService = new NotificationsRecipientService(pnDeliveryClientRecipient, pnDeliveryPushClient, pnBffExceptionUtility, pnEmdClient);
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
                Mockito.any(NotificationStatusV26.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationsReceivedMock.getNotificationReceivedPNMock()));

        Mono<BffNotificationsResponse> result = notificationsRecipientService.searchReceivedNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PG,
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
                Mockito.any(NotificationStatusV26.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffNotificationsResponse> result = notificationsRecipientService.searchReceivedNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PG,
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
                Mockito.any(NotificationStatusV26.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.just(notificationsReceivedMock.getNotificationReceivedPNMock()));

        Mono<BffNotificationsResponse> result = notificationsRecipientService.searchReceivedDelegatedNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PG,
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
                Mockito.any(NotificationStatusV26.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.any(OffsetDateTime.class),
                Mockito.anyInt(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffNotificationsResponse> result = notificationsRecipientService.searchReceivedDelegatedNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PG,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                UserMock.PN_CX_GROUPS,
                "MANDATE_ID"
        );

        StepVerifier.create(result)
                .expectNext(NotificationReceivedDetailMapper.modelMapper.mapReceivedNotificationDetail(notificationDetailRecipientMock.getNotificationMultiRecipientMock()))
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
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

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.AAR,
                null,
                "aar-id",
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

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.AAR,
                null,
                "aar-id",
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
        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.AAR,
                null,
                null,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
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

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.LEGAL_FACT,
                null,
                "legal-fact-id",
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
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(UUID.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.LEGAL_FACT,
                null,
                "legal-fact-id",
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
        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.LEGAL_FACT,
                null,
                null,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
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
        when(pnDeliveryClientRecipient.getReceivedNotificationDocument(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyList(),
                Mockito.any(UUID.class)
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getRecipientAttachmentMock()));

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.ATTACHMENT,
                0,
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

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.ATTACHMENT,
                0,
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
        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationDocument(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                BffDocumentType.ATTACHMENT,
                null,
                null,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
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
        when(pnDeliveryClientRecipient.getReceivedNotificationPayment(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(UUID.class),
                Mockito.anyInt()
        )).thenReturn(Mono.just(notificationDownloadDocumentMock.getRecipientAttachmentMock()));

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationPayment(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                "PAGOPA",
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID(),
                0
        );

        StepVerifier.create(result)
                .expectNext(NotificationDownloadDocumentMapper.modelMapper.mapSentAttachmentDownloadResponse(notificationDownloadDocumentMock.getPaAttachmentMock()))
                .verifyComplete();
    }

    @Test
    void getNotificationPaymentError() {
        when(pnDeliveryClientRecipient.getReceivedNotificationPayment(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(UUID.class),
                Mockito.anyInt()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffDocumentDownloadMetadataResponse> result = notificationsRecipientService.getReceivedNotificationPayment(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                "IUN",
                "PAGOPA",
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID(),
                0
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void checkAarQrCode() {
        when(pnDeliveryClientRecipient.checkAarQrCode(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.just(notificationsReceivedMock.getResponseCheckAarMandateDtoPNMock()));

        Mono<BffCheckAarResponse> result = notificationsRecipientService.checkAarQrCode(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                Mono.just(notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock()),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectNext(NotificationAarQrCodeMapper.modelMapper.toBffResponseCheckAarMandateDto(notificationsReceivedMock.getResponseCheckAarMandateDtoPNMock()))
                .verifyComplete();
    }

    @Test
    void checkAarQrCodeError() {
        when(pnDeliveryClientRecipient.checkAarQrCode(
                Mockito.anyString(),
                Mockito.any(it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffCheckAarResponse> result = notificationsRecipientService.checkAarQrCode(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                Mono.just(notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock()),
                UserMock.PN_CX_GROUPS
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}