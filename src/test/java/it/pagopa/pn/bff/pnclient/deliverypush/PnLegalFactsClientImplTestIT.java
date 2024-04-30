package it.pagopa.pn.bff.pnclient.deliverypush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactCategory;
import it.pagopa.pn.bff.mocks.NotificationLegalFactMock;
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
class PnLegalFactsClientImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String iun = "DHUJ-QYVT-DMVH-202302-P-1";
    private final LegalFactCategory legalFactCategory = LegalFactCategory.DIGITAL_DELIVERY;
    private final String legalFactId = "LEGAL_FACT_ID";
    private final String path = "/delivery-push/" + iun + "/legal-facts/" + legalFactCategory + "/" + legalFactId;
    private final NotificationLegalFactMock notificationLegalFactMock = new NotificationLegalFactMock();
    @Autowired
    private PnLegalFactsClientImpl pnLegalFactsClient;

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
    void getLegalFact() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(notificationLegalFactMock.getLegalFactMock());
        mockServerClient.when(request().withMethod("GET").withPath(path))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnLegalFactsClient.getLegalFact(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                iun,
                legalFactCategory,
                legalFactId,
                UserMock.PN_CX_GROUPS,
                UUID.randomUUID()
        )).expectNext(notificationLegalFactMock.getLegalFactMock()).verifyComplete();
    }

    @Test
    void getLegalFactError() {
        mockServerClient.when(request().withMethod("GET").withPath(path))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnLegalFactsClient.getLegalFact(
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
}