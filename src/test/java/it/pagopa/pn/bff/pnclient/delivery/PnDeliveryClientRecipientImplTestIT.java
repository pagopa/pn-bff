package it.pagopa.pn.bff.pnclient.delivery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus;
import it.pagopa.pn.bff.mappers.notifications.NotificationsReceivedMapper;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
import it.pagopa.pn.bff.mocks.UserMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PnDeliveryClientRecipientImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String iun = "DHUJ-QYVT-DMVH-202302-P-1";
    private final Integer docIdx = 0;
    private final String attachmentName = "PAGOPA";
    private final String notificationListPath = "/delivery/notifications/received";
    private final String notificationDetailPath = "/delivery/v2.3/notifications/received/" + iun;
    private final String notificationQrCodePath = "/delivery/notifications/received/check-aar-qr-code";
    private final String documentDownloadPath = "/delivery/notifications/received/" + iun + "/attachments/documents/" + docIdx;

    private final String paymentDownloadPath = "/delivery/notifications/received/" + iun + "/attachments/payment/" + attachmentName;
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    @Autowired
    private PnDeliveryClientRecipientImpl pnDeliveryClient;

    @BeforeAll
    public static void startMockServer() {
        mockServer = startClientAndServer(9998);
        mockServerClient = new MockServerClient("localhost", 9998);
    }

    @AfterAll
    public static void stopMockServer() {
        mockServerClient.close();
        mockServer.stop();
    }

    @AfterEach
    public void resetMockServerClient() {
        mockServerClient.reset();
    }

    @Test
    void searchReceivedNotifications() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        String response = objectMapper.writeValueAsString(notificationsReceivedMock.getNotificationReceivedPNMock());
        mockServerClient.when(request().withMethod("GET").withPath(notificationListPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.searchReceivedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.MANDATE_ID,
                NotificationsReceivedMock.SENDER_ID,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SUBJECT_REG_EXP,
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        )).expectNext(notificationsReceivedMock.getNotificationReceivedPNMock()).verifyComplete();
    }

    @Test
    void searchReceivedNotificationsError() {
        mockServerClient.when(request().withMethod("GET").withPath(notificationListPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.searchReceivedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.MANDATE_ID,
                NotificationsReceivedMock.SENDER_ID,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SUBJECT_REG_EXP,
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void searchReceivedDelegatedNotifications() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        String response = objectMapper.writeValueAsString(notificationsReceivedMock.getNotificationReceivedPNMock());
        mockServerClient.when(request().withMethod("GET").withPath(notificationListPath + "/delegated"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.searchReceivedDelegatedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.SENDER_ID,
                NotificationsReceivedMock.RECIPIENT_ID,
                NotificationsReceivedMock.GROUP,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        )).expectNext(notificationsReceivedMock.getNotificationReceivedPNMock()).verifyComplete();
    }

    @Test
    void searchReceivedDelegatedNotificationsError() {
        mockServerClient.when(request().withMethod("GET").withPath(notificationListPath + "/delegated"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.searchReceivedDelegatedNotifications(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                NotificationsReceivedMock.IUN_MATCH,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.SENDER_ID,
                NotificationsReceivedMock.RECIPIENT_ID,
                NotificationsReceivedMock.GROUP,
                NotificationStatus.ACCEPTED,
                OffsetDateTime.parse(NotificationsReceivedMock.START_DATE),
                OffsetDateTime.parse(NotificationsReceivedMock.END_DATE),
                NotificationsReceivedMock.SIZE,
                NotificationsReceivedMock.NEXT_PAGES_KEY
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getReceivedNotification() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        String response = objectMapper.writeValueAsString(notificationDetailRecipientMock.getNotificationMultiRecipientMock());
        mockServerClient.when(request().withMethod("GET").withPath(notificationDetailPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.getReceivedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                iun,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.MANDATE_ID
        )).expectNext(notificationDetailRecipientMock.getNotificationMultiRecipientMock()).verifyComplete();
    }

    @Test
    void getSentNotificationV23Error() {
        mockServerClient.when(request().withMethod("GET").withPath(notificationDetailPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.getReceivedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                iun,
                UserMock.PN_CX_GROUPS,
                NotificationsReceivedMock.MANDATE_ID
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getReceivedNotificationDocument() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(notificationDownloadDocumentMock.getRecipientAttachmentMock());
        mockServerClient.when(request().withMethod("GET").withPath(documentDownloadPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.getReceivedNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                iun,
                docIdx,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectNext(notificationDownloadDocumentMock.getRecipientAttachmentMock()).verifyComplete();
    }

    @Test
    void getReceivedNotificationDocumentError() {
        mockServerClient.when(request().withMethod("GET").withPath(documentDownloadPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.getReceivedNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                iun,
                docIdx,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectError().verify();
    }

    @Test
    void getReceivedNotificationPayment() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(notificationDownloadDocumentMock.getRecipientAttachmentMock());
        mockServerClient.when(request().withMethod("GET").withPath(paymentDownloadPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.getReceivedNotificationPayment(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                iun,
                attachmentName,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID(),
                0
        )).expectNext(notificationDownloadDocumentMock.getRecipientAttachmentMock()).verifyComplete();
    }

    @Test
    void getReceivedNotificationPaymentError() {
        mockServerClient.when(request().withMethod("GET").withPath(paymentDownloadPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.getReceivedNotificationPayment(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                iun,
                attachmentName,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID(),
                0
        )).expectError().verify();
    }

    @Test
    void checkAarQrCode() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(notificationsReceivedMock.getResponseCheckAarMandateDtoPNMock());
        mockServerClient.when(request().withMethod("POST").withPath(notificationQrCodePath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.checkAarQrCode(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationsReceivedMapper.modelMapper.toRequestCheckAarMandateDto(notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock()),
                UserMock.PN_CX_GROUPS
        )).expectNext(notificationsReceivedMock.getResponseCheckAarMandateDtoPNMock()).verifyComplete();
    }

    @Test
    void checkAarQrCodeError() {
        mockServerClient.when(request().withMethod("POST").withPath(notificationQrCodePath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.checkAarQrCode(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                NotificationsReceivedMapper.modelMapper.toRequestCheckAarMandateDto(notificationsReceivedMock.getRequestCheckAarMandateDtoPNMock()),
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }
}