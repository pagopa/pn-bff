package it.pagopa.pn.bff.pnclient.deliverypush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactCategory;
import it.pagopa.pn.bff.mocks.NotificationDownloadDocumentMock;
import it.pagopa.pn.bff.mocks.NotificationsSentMock;
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
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PnDeliveryPushClientImplTestIT {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String iun = "DHUJ-QYVT-DMVH-202302-P-1";
    private final LegalFactCategory legalFactCategory = LegalFactCategory.DIGITAL_DELIVERY;
    private final String legalFactId = "LEGAL_FACT_ID";
    private final String documentPath = "/delivery-push/" + iun + "/document/" + DocumentCategory.AAR;
    private final String legalFactPath = "/delivery-push/" + iun + "/legal-facts/" + legalFactCategory + "/" + legalFactId;
    private final String cancellationPath = "/delivery-push/v2.0/notifications/cancel/" + iun;
    private final NotificationDownloadDocumentMock notificationDownloadDocumentMock = new NotificationDownloadDocumentMock();
    private final NotificationsSentMock notificationsSentMock = new NotificationsSentMock();
    @Autowired
    private PnDeliveryPushClientImpl pnDeliveryPushClient;

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
    void getDocumentsWeb() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(notificationDownloadDocumentMock.getDocumentMock());
        mockServerClient.when(request().withMethod("GET").withPath(documentPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryPushClient.getDocumentsWeb(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                DocumentCategory.AAR,
                "DOCUMENT_ID",
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectNext(notificationDownloadDocumentMock.getDocumentMock()).verifyComplete();
    }

    @Test
    void getDocumentsWebError() {
        mockServerClient.when(request().withMethod("GET").withPath(documentPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryPushClient.getDocumentsWeb(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                DocumentCategory.AAR,
                "DOCUMENT_ID",
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectError().verify();
    }

    @Test
    void getLegalFact() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(notificationDownloadDocumentMock.getLegalFactMock());
        mockServerClient.when(request().withMethod("GET").withPath(legalFactPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryPushClient.getLegalFact(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                legalFactCategory,
                legalFactId,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectNext(notificationDownloadDocumentMock.getLegalFactMock()).verifyComplete();
    }

    @Test
    void getLegalFactError() {
        mockServerClient.when(request().withMethod("GET").withPath(legalFactPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryPushClient.getLegalFact(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                legalFactCategory,
                legalFactId,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectError().verify();
    }

    @Test
    void notificationCancellation() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(notificationsSentMock.notificationCancellationPNMock());
        mockServerClient.when(request().withMethod("PUT").withPath(cancellationPath))
                .respond(response()
                        .withStatusCode(202)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDeliveryPushClient.notificationCancellation(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                UserMock.PN_CX_GROUPS
        )).expectNext(notificationsSentMock.notificationCancellationPNMock()).verifyComplete();
    }

    @Test
    void notificationCancellationError() {
        mockServerClient.when(request().withMethod("PUT").withPath(cancellationPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryPushClient.notificationCancellation(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }
}