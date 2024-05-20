package it.pagopa.pn.bff.pnclient.delivery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatus;
import it.pagopa.pn.bff.mocks.*;
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
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PnDeliveryClientPAImplTestIT {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String iun = "DHUJ-QYVT-DMVH-202302-P-1";
    private final Integer docIdx = 0;
    private final Integer recipientIdx = 0;
    private final String attachmentName = "PAGOPA";
    private final String notificationsListPath = "/delivery/notifications/sent";
    private final String notificationDetailPath = "/delivery/v2.3/notifications/sent/" + iun;
    private final String documentDownloadPath = "/delivery/notifications/sent/" + iun + "/attachments/documents/" + docIdx;
    private final String paymentDownloadPath = "/delivery/notifications/sent/" + iun + "/attachments/payment/" + recipientIdx + "/" + attachmentName;
    private final String newNotificationPath = "/delivery/v2.3/requests";
    private final NotificationsSentMock notificationsSentMock = new NotificationsSentMock();
    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    private final NewSentNotificationMock newSentNotificationMock = new NewSentNotificationMock();
    @Autowired
    private PnDeliveryClientPAImpl pnDeliveryClient;

    @BeforeAll
    public static void startMockServer() {
        mockServer = startClientAndServer(9998);
        mockServerClient = new MockServerClient("localhost", 9998);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
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
    void searchSentNotifications() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(notificationsSentMock.getNotificationSentPNMock());
        mockServerClient.when(request().withMethod("GET").withPath(notificationsListPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.searchSentNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                OffsetDateTime.parse(NotificationsSentMock.START_DATE),
                OffsetDateTime.parse(NotificationsSentMock.END_DATE),
                UserMock.PN_CX_GROUPS,
                NotificationsSentMock.RECIPIENT_ID,
                NotificationStatus.ACCEPTED,
                NotificationsSentMock.SUBJECT_REG_EXP,
                NotificationsSentMock.IUN_MATCH,
                NotificationsSentMock.SIZE,
                NotificationsSentMock.NEXT_PAGES_KEY
        )).expectNext(notificationsSentMock.getNotificationSentPNMock()).verifyComplete();
    }

    @Test
    void searchSentNotificationsError() {
        mockServerClient.when(request().withMethod("GET").withPath(notificationsListPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.searchSentNotifications(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                OffsetDateTime.parse(NotificationsSentMock.START_DATE),
                OffsetDateTime.parse(NotificationsSentMock.END_DATE),
                UserMock.PN_CX_GROUPS,
                NotificationsSentMock.RECIPIENT_ID,
                NotificationStatus.ACCEPTED,
                NotificationsSentMock.SUBJECT_REG_EXP,
                NotificationsSentMock.IUN_MATCH,
                NotificationsSentMock.SIZE,
                NotificationsSentMock.NEXT_PAGES_KEY
        )).expectError().verify();
    }

    @Test
    void getSentNotification() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(notificationDetailPaMock.getNotificationMultiRecipientMock());
        mockServerClient.when(request().withMethod("GET").withPath(notificationDetailPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.getSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                UserMock.PN_CX_GROUPS
        )).expectNext(notificationDetailPaMock.getNotificationMultiRecipientMock()).verifyComplete();
    }

    @Test
    void getSentNotificationError() {
        mockServerClient.when(request().withMethod("GET").withPath(notificationDetailPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.getSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void getSentNotificationDocument() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(notificationDownloadDocumentMock.getPaAttachmentMock());
        mockServerClient.when(request().withMethod("GET").withPath(documentDownloadPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.getSentNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                docIdx,
                UserMock.PN_CX_GROUPS
        )).expectNext(notificationDownloadDocumentMock.getPaAttachmentMock()).verifyComplete();
    }

    @Test
    void getSentNotificationDocumentError() {
        mockServerClient.when(request().withMethod("GET").withPath(documentDownloadPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.getSentNotificationDocument(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                docIdx,
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void getSentNotificationPayment() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(notificationDownloadDocumentMock.getPaAttachmentMock());
        mockServerClient.when(request().withMethod("GET").withPath(paymentDownloadPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.getSentNotificationPayment(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                recipientIdx,
                attachmentName,
                UserMock.PN_CX_GROUPS,
                0
        )).expectNext(notificationDownloadDocumentMock.getPaAttachmentMock()).verifyComplete();
    }

    @Test
    void getSentNotificationPaymentError() {
        mockServerClient.when(request().withMethod("GET").withPath(paymentDownloadPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.getSentNotificationPayment(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                recipientIdx,
                attachmentName,
                UserMock.PN_CX_GROUPS,
                0
        )).expectError().verify();
    }

    @Test
    void newSentNotification() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(newSentNotificationMock.getNewSentNotificationRequest());
        String response = objectMapper.writeValueAsString(newSentNotificationMock.getNewSentNotificationResponse());
        mockServerClient.when(request()
                        .withMethod("POST")
                        .withPath(newNotificationPath)
                        .withBody(request)
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryClient.newSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                newSentNotificationMock.getNewSentNotificationRequest(),
                UserMock.PN_CX_GROUPS
        )).expectNext(newSentNotificationMock.getNewSentNotificationResponse()).verifyComplete();
    }

    @Test
    void newSentNotificationError() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(newSentNotificationMock.getNewSentNotificationRequest());
        mockServerClient.when(request()
                        .withMethod("POST")
                        .withPath(newNotificationPath)
                        .withBody(request)
                )
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.newSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                newSentNotificationMock.getNewSentNotificationRequest(),
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }
}