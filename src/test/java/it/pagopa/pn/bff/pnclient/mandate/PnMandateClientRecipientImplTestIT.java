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
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String pathMandate = "/mandate/api/v1";
    private final String mandateId = "MANDATE_ID";
    private final MandateMock mandateMock = new MandateMock();
    @Autowired
    private PnMandateClientRecipientImpl pnMandateClient;

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
    void countMandatesByDelegate() throws JsonProcessingException {
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

    @Test
    void acceptMandate() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(mandateMock.getAcceptRequestMock());
        mockServerClient.when(request().withMethod("PATCH").withPath(pathMandate + "/mandate/" + mandateId + "/accept").withBody(request))
                .respond(response()
                        .withStatusCode(200)
                );

        StepVerifier.create(pnMandateClient.acceptMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getAcceptRequestMock()
        )).expectNext().verifyComplete();
    }

    @Test
    void acceptMandateError() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(mandateMock.getAcceptRequestMock());
        mockServerClient.when(request().withMethod("PATCH").withPath(pathMandate + "/mandate/" + mandateId + "/accept").withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnMandateClient.acceptMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getAcceptRequestMock()
        )).expectError().verify();
    }

    @Test
    void updateMandate() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(mandateMock.getUpdateRequestMock());
        mockServerClient.when(request().withMethod("PATCH").withPath(pathMandate + "/mandate/" + mandateId + "/update").withBody(request))
                .respond(response()
                        .withStatusCode(200)
                );

        StepVerifier.create(pnMandateClient.updateMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getUpdateRequestMock()
        )).expectNext().verifyComplete();
    }

    @Test
    void updateMandateError() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(mandateMock.getUpdateRequestMock());
        mockServerClient.when(request().withMethod("PATCH").withPath(pathMandate + "/mandate/" + mandateId + "/update").withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnMandateClient.updateMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                mandateMock.getUpdateRequestMock()
        )).expectError().verify();
    }

    @Test
    void rejectMandate() {
        mockServerClient.when(request().withMethod("PATCH").withPath(pathMandate + "/mandate/" + mandateId + "/reject"))
                .respond(response()
                        .withStatusCode(200)
                );

        StepVerifier.create(pnMandateClient.rejectMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext().verifyComplete();
    }

    @Test
    void rejectMandateError() {
        mockServerClient.when(request().withMethod("PATCH").withPath(pathMandate + "/mandate/" + mandateId + "/reject"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnMandateClient.rejectMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }

    @Test
    void revokeMandate() {
        mockServerClient.when(request().withMethod("PATCH").withPath(pathMandate + "/mandate/" + mandateId + "/revoke"))
                .respond(response()
                        .withStatusCode(200)
                );

        StepVerifier.create(pnMandateClient.revokeMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext().verifyComplete();
    }

    @Test
    void revokeMandateError() {
        mockServerClient.when(request().withMethod("PATCH").withPath(pathMandate + "/mandate/" + mandateId + "/revoke"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnMandateClient.revokeMandate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                mandateId,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }

    @Test
    void getMandatesByDelegate() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(mandateMock.getMandatesByDelegateMock());
        mockServerClient.when(request().withMethod("GET")
                        .withPath(pathMandate + "/mandates-by-delegate")
                        .withQueryStringParameter("status", "pending"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnMandateClient.getMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "pending"
        )).expectNextSequence(mandateMock.getMandatesByDelegateMock()).verifyComplete();
    }

    @Test
    void getMandatesByDelegateError() {
        mockServerClient.when(request().withMethod("GET")
                        .withPath(pathMandate + "/mandates-by-delegate")
                        .withQueryStringParameter("status", "pending"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnMandateClient.getMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "pending"
        )).expectError().verify();
    }

    @Test
    void searchMandatesByDelegate() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(mandateMock.getSearchMandatesByDelegateRequestMock());
        String response = objectMapper.writeValueAsString(mandateMock.getSearchMandatesByDelegateResponseMock());
        mockServerClient.when(request().withMethod("POST")
                        .withPath(pathMandate + "/mandates-by-delegate")
                        .withQueryStringParameter("size", "10")
                        .withQueryStringParameter("nextPageKey", "NEXT_PAGE")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(request)
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnMandateClient.searchMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                10,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "NEXT_PAGE",
                mandateMock.getSearchMandatesByDelegateRequestMock()
        )).expectNext(mandateMock.getSearchMandatesByDelegateResponseMock()).verifyComplete();
    }

    @Test
    void searchMandatesByDelegateError() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(mandateMock.getSearchMandatesByDelegateRequestMock());
        mockServerClient.when(request().withMethod("POST")
                        .withPath(pathMandate + "/mandates-by-delegate")
                        .withQueryStringParameter("size", "10")
                        .withQueryStringParameter("nextPageKey", "NEXT_PAGE")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(request)
                )
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnMandateClient.searchMandatesByDelegate(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PF,
                10,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                "NEXT_PAGE",
                mandateMock.getSearchMandatesByDelegateRequestMock()
        )).expectError().verify();
    }
}