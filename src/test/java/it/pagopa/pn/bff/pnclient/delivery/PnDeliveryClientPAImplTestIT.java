package it.pagopa.pn.bff.pnclient.delivery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.api.SenderReadWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatus;
import it.pagopa.pn.bff.mocks.NotificationDetailPaMock;
import it.pagopa.pn.bff.mocks.NotificationSentMock;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PnDeliveryClientPAImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String iun = "DHUJ-QYVT-DMVH-202302-P-1";
    private final String pathWithIun = "/delivery/v2.3/notifications/sent/" + iun;
    private final String pathWithoutIun = "/delivery/notifications/sent";
    private final NotificationDetailPaMock notificationDetailPaMock = new NotificationDetailPaMock();
    private final NotificationSentMock notificationSentMock = new NotificationSentMock();
    @Autowired
    private PnDeliveryClientPAImpl paDeliveryClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.SenderReadB2BApi")
    private SenderReadB2BApi senderReadB2BApi;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.api.SenderWriteB2BApi")
    private SenderReadWebApi senderReadWebApi;

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
    void getSentNotification() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        String response = objectMapper.writeValueAsString(notificationDetailPaMock.getNotificationMultiRecipientMock());
        mockServerClient.when(request().withMethod("GET").withPath(pathWithIun))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(paDeliveryClient.getSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                UserMock.PN_CX_GROUPS
        )).expectNext(notificationDetailPaMock.getNotificationMultiRecipientMock()).verifyComplete();
    }

    @Test
    void getSentNotificationError() {
        mockServerClient.when(request().withMethod("GET").withPath(pathWithIun))
                .respond(response().withStatusCode(404));

        StepVerifier.create(paDeliveryClient.getSentNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void searchSentNotifications() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        String response = objectMapper.writeValueAsString(notificationSentMock.getNotificationSentPNMock());
        mockServerClient.when(request().withMethod("GET").withPath(pathWithoutIun))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(paDeliveryClient.searchSentNotification(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                UserMock.PN_CX_GROUPS,
                "RECIPIENT_ID",
                NotificationStatus.ACCEPTED,
                "SUBJECT",
                "IUN",
                10,
                "NEXT_PAGES_KEY"
        )).expectNext(notificationSentMock.getNotificationSentPNMock()).verifyComplete();
    }

    @Test
    void searchSentNotificationsError() {
        mockServerClient.when(request().withMethod("GET").withPath(pathWithoutIun))
                .respond(response().withStatusCode(404));

        StepVerifier.create(paDeliveryClient.searchSentNotification(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                UserMock.PN_CX_GROUPS,
                "RECIPIENT_ID",
                NotificationStatus.ACCEPTED,
                "SUBJECT",
                "IUN",
                10,
                "NEXT_PAGES_KEY"
        )).expectError().verify();
    }
}