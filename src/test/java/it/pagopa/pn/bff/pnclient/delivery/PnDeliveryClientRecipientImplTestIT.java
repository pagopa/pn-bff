package it.pagopa.pn.bff.pnclient.delivery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.NotificationDetailRecipientMock;
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

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PnDeliveryClientRecipientImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String iun = "DHUJ-QYVT-DMVH-202302-P-1";
    private final String mandateId = "MANDATE_ID";
    private final String path = "/delivery/v2.3/notifications/received/" + iun;
    private final NotificationDetailRecipientMock notificationDetailRecipientMock = new NotificationDetailRecipientMock();
    @Autowired
    private PnDeliveryClientRecipientImpl pnDeliveryClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi")
    private RecipientReadApi recipientReadApi;

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
    void getReceivedNotification() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        String response = objectMapper.writeValueAsString(notificationDetailRecipientMock.getNotificationMultiRecipientMock());
        mockServerClient.when(request().withMethod("GET").withPath(path))
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
                mandateId
        )).expectNext(notificationDetailRecipientMock.getNotificationMultiRecipientMock()).verifyComplete();
    }

    @Test
    void getSentNotificationV23Error() {
        mockServerClient.when(request().withMethod("GET").withPath(path))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDeliveryClient.getReceivedNotification(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                iun,
                UserMock.PN_CX_GROUPS,
                mandateId
        )).expectError(PnBffException.class).verify();
    }
}