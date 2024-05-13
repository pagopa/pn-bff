package it.pagopa.pn.bff.pnclient.mandate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.MandateMock;
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

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PnMandateClientRecipientImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String pathMandate = "/mandate/api/v1";
    private final MandateMock mandateMock = new MandateMock();
    @Autowired
    private PnMandateClientRecipientImpl pnMandateClient;

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
    void countMandatesByDelegate() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(mandateMock.getCountMock());
        mockServerClient.when(request().withMethod("GET").withPath(pathMandate + "/count-by-delegate"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnMandateClient.countMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "STATUS"
        )).expectNext(mandateMock.getCountMock()).verifyComplete();
    }

    @Test
    void countMandatesByDelegateError() {
        mockServerClient.when(request().withMethod("GET").withPath(pathMandate + "/count-by-delegate"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnMandateClient.countMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "STATUS"
        )).expectError().verify();
    }

    @Test
    void createMandate() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        String request = objectMapper.writeValueAsString(mandateMock.getNewMandateRequestMock());
        String response = objectMapper.writeValueAsString(mandateMock.getNewMandateResponseMock());
        mockServerClient.when(request().withMethod("POST").withPath(pathMandate + "/mandate").withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnMandateClient.createMandate(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getNewMandateRequestMock()
        )).expectNext(mandateMock.getNewMandateResponseMock()).verifyComplete();
    }

    @Test
    void createMandateError() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        String request = objectMapper.writeValueAsString(mandateMock.getNewMandateRequestMock());
        mockServerClient.when(request().withMethod("POST").withPath(pathMandate + "/mandate").withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnMandateClient.createMandate(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getNewMandateRequestMock()
        )).expectError().verify();
    }
}