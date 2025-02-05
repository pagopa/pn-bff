package it.pagopa.pn.bff.pnclient.emd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.mocks.NotificationsReceivedMock;
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
class PnEmdClientImplTestIT {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final NotificationsReceivedMock notificationsReceivedMock = new NotificationsReceivedMock();
    private final String checkTppPath = "/emd-integration-private/emd/check-tpp";

    @Autowired
    private PnEmdClientImpl pnEmdClient;

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
    void resetServer() {
        mockServer.reset();
    }

    @Test
    void testCheckTpp() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(notificationsReceivedMock.getRetrievalPayloadMock());

        mockServerClient.when(request().withMethod("GET").withPath(checkTppPath)
                        .withQueryStringParameter("retrievalId", "retrievalId"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnEmdClient.checkTpp("retrievalId"))
                .expectNextMatches(payload ->
                        "retrievalId".equals(payload.getRetrievalId()) &&
                                "tppId".equals(payload.getTppId()) &&
                                "deeplink".equals(payload.getDeeplink()) &&
                                "originId".equals(payload.getOriginId()) &&
                                "paymentButton".equals(payload.getPaymentButton()))
                .verifyComplete();
    }

    @Test
    void testCheckTppError() {
        mockServerClient.when(request().withMethod("GET").withPath(checkTppPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnEmdClient.checkTpp("retrievalId")).expectError().verify();
    }
}