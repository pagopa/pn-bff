package it.pagopa.pn.bff.pnclient.apikeys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.PublicKeysMock;
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
class PnPublicKeyManagerClientPGImplTestIT {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String path = "/pg-self/public-keys";
    private final PublicKeysMock publicKeysMock = new PublicKeysMock();

    @Autowired
    private PnPublicKeyManagerClientPGImpl pnPublicKeyManagerClientPG;

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
    public void resetMockServerClient() { mockServerClient.reset(); }

    @Test
    void getPublicKeys() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(publicKeysMock.getPublicKeysMock());
        mockServerClient.when(request().withMethod("GET").withPath(path))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnPublicKeyManagerClientPG.getPublicKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "CREATED_AT",
                true
        )).expectNext(publicKeysMock.getPublicKeysMock()).verifyComplete();
    }

    @Test
    void getPublicKeysError() {
        mockServerClient.when(request().withMethod("GET").withPath(path))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnPublicKeyManagerClientPG.getPublicKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "CREATED_AT",
                true
        )).expectError().verify();
    }

    @Test
    void newPublicKey() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(publicKeysMock.gePublicKeyRequestMock());
        String response = objectMapper.writeValueAsString(publicKeysMock.gePublicKeyResponseMock());
        mockServerClient.when(request().withMethod("POST").withPath(path).withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnPublicKeyManagerClientPG.newPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                publicKeysMock.gePublicKeyRequestMock(),
                UserMock.PN_CX_GROUPS
        )).expectNext(publicKeysMock.gePublicKeyResponseMock()).verifyComplete();
    }

    @Test
    void newPublicKeyError() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(publicKeysMock.gePublicKeyRequestMock());
        mockServerClient.when(request().withMethod("POST").withPath(path).withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnPublicKeyManagerClientPG.newPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                publicKeysMock.gePublicKeyRequestMock(),
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void deletePublicKey() {
        mockServerClient.when(request().withMethod("DELETE").withPath(path + "/PUBLIC_KEY_ID"))
                .respond(response()
                        .withStatusCode(204)
                        .withContentType(MediaType.APPLICATION_JSON)
                );

        StepVerifier.create(pnPublicKeyManagerClientPG.deletePublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                UserMock.PN_CX_GROUPS
        )).expectNext().verifyComplete();
    }

    @Test
    void deletePublicKeyError() {
        mockServerClient.when(request().withMethod("DELETE").withPath(path + "/PUBLIC_KEY_ID"))
                .respond(response()
                        .withStatusCode(404)
                );

        StepVerifier.create(pnPublicKeyManagerClientPG.deletePublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void changeStatusPublicKey() throws JsonProcessingException {
        mockServerClient.when(request().withMethod("PUT")
                        .withPath(path + "/PUBLIC_KEY_ID/status")
                        .withQueryStringParameter("status", "BLOCK"))
                .respond(response()
                        .withStatusCode(204)
                );

        StepVerifier.create(pnPublicKeyManagerClientPG.changeStatusPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                "BLOCK",
                UserMock.PN_CX_GROUPS
        )).expectNext().verifyComplete();
    }

    @Test
    void changeStatusPublicKeyError() {
        mockServerClient.when(request().withMethod("PUT")
                        .withPath(path + "/PUBLIC_KEY_ID/status")
                        .withQueryStringParameter("status", "BLOCK"))
                .respond(response()
                        .withStatusCode(404)
                );

        StepVerifier.create(pnPublicKeyManagerClientPG.changeStatusPublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                "BLOCK",
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void rotatePublicKey() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(publicKeysMock.gePublicKeyRequestMock());
        String response = objectMapper.writeValueAsString(publicKeysMock.gePublicKeyResponseMock());
        mockServerClient.when(request().withMethod("POST").withPath(path + "/PUBLIC_KEY_ID/rotate").withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnPublicKeyManagerClientPG.rotatePublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                publicKeysMock.gePublicKeyRequestMock(),
                UserMock.PN_CX_GROUPS
        )).expectNext(publicKeysMock.gePublicKeyResponseMock()).verifyComplete();
    }

    @Test
    void rotatePublicKeyError() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(publicKeysMock.gePublicKeyRequestMock());
        String response = objectMapper.writeValueAsString(publicKeysMock.gePublicKeyResponseMock());
        mockServerClient.when(request().withMethod("POST").withPath(path + "/PUBLIC_KEY_ID/rotate").withBody(request))
                .respond(response()
                        .withStatusCode(404)
                );

        StepVerifier.create(pnPublicKeyManagerClientPG.rotatePublicKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_ROLE,
                "PUBLIC_KEY_ID",
                publicKeysMock.gePublicKeyRequestMock(),
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void getIssuerStatusPublicKeys() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(publicKeysMock.getIssuerStatusPublicKeysResponseMock());
        mockServerClient.when(request().withMethod("GET").withPath(path + "/issuer/status"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnPublicKeyManagerClientPG.getIssuerStatus(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID
        )).expectNext(publicKeysMock.getIssuerStatusPublicKeysResponseMock()).verifyComplete();
    }

    @Test
    void getIssuerStatusPublicKeysError() {
        mockServerClient.when(request().withMethod("GET").withPath(path + "/issuer/status"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnPublicKeyManagerClientPG.getIssuerStatus(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID
        )).expectError().verify();
    }
}
