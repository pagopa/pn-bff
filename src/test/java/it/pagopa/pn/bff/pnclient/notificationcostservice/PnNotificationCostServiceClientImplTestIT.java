package it.pagopa.pn.bff.pnclient.notificationcostservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.mocks.NotificationCostDetailsMock;
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

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PnNotificationCostServiceClientImplTestIT {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;

    private final NotificationCostDetailsMock notificationCostMock = new NotificationCostDetailsMock();
    private final String notificationCostPath = "/notification-cost-private/cost/test-iun/recipient/0";

    @Autowired
    private PnNotificationCostServiceClientImpl pnNotificationCostServiceClient;

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
    void resetServer() {
        mockServer.reset();
    }

    @Test
    void testGetNotificationCostRecipient() throws JsonProcessingException {
        String responseBody = objectMapper.writeValueAsString(
                notificationCostMock.getNotificationCostRecipientResponseMock()
        );

        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath(notificationCostPath))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(responseBody)
                );

        StepVerifier.create(pnNotificationCostServiceClient.getNotificationCostRecipient("test-iun", 0))
                .expectNextMatches(result ->
                        result.getTotalCost().getCostWithVat() == 1220 &&
                                result.getTotalCost().getDetails().getBaseCostDetail().getCost() == 100 &&
                                result.getTotalCost().getDetails().getAnalogCostDetail().getCostWithVat() == 1000
                )
                .verifyComplete();
    }

    @Test
    void testGetNotificationCostRecipientError() {
        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath(notificationCostPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnNotificationCostServiceClient.getNotificationCostRecipient("test-iun", 0))
                .expectError()
                .verify();
    }
}