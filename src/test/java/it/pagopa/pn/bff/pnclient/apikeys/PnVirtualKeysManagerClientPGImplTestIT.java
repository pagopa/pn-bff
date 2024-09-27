package it.pagopa.pn.bff.pnclient.apikeys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestVirtualKeyStatus;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.mocks.VirtualKeysMock;
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
class PnVirtualKeysManagerClientPGImplTestIT {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String path = "/pg-self/virtual-keys";
    private final VirtualKeysMock virtualKeysMock = new VirtualKeysMock();

    @Autowired
    private PnVirtualKeysManagerClientPGImpl pnVirtualKeysManagerClientPGImpl;

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
    void getVirtualKeys() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(virtualKeysMock.getVirtualKeysMock());
        mockServerClient.when(request().withMethod("GET").withPath(path))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnVirtualKeysManagerClientPGImpl.getVirtualKeys(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        )).expectNext(virtualKeysMock.getVirtualKeysMock()).verifyComplete();
    }

    @Test
    void getVirtualKeysError() {
        mockServerClient.when(request().withMethod("GET").withPath(path))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnVirtualKeysManagerClientPGImpl.getVirtualKeys(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        )).expectError().verify();
    }

    @Test
    void newVirtualKey() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(virtualKeysMock.getRequestNewVirtualKeyMock());
        String response = objectMapper.writeValueAsString(virtualKeysMock.getResponseNewVirtualKeyMock());
        mockServerClient.when(request().withMethod("POST").withPath(path).withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnVirtualKeysManagerClientPGImpl.newVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                virtualKeysMock.getRequestNewVirtualKeyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext(virtualKeysMock.getResponseNewVirtualKeyMock()).verifyComplete();
    }

    @Test
    void newVirtualKeyError() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(virtualKeysMock.getRequestNewVirtualKeyMock());
        mockServerClient.when(request().withMethod("POST").withPath(path).withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnVirtualKeysManagerClientPGImpl.newVirtualKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                virtualKeysMock.getRequestNewVirtualKeyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }

    @Test
    void deleteVirtualKey() {
        mockServerClient.when(request().withMethod("DELETE").withPath(path + "/VIRTUALKEY_ID"))
                .respond(response()
                        .withStatusCode(204)
                        .withContentType(MediaType.APPLICATION_JSON)
                );

        StepVerifier.create(pnVirtualKeysManagerClientPGImpl.deleteVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                "VIRTUALKEY_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext().verifyComplete();
    }

    @Test
    void deleteVirtualKeyError() {
        mockServerClient.when(request().withMethod("DELETE").withPath(path + "/VIRTUALKEY_ID"))
                .respond(response()
                        .withStatusCode(404)
                );

        StepVerifier.create(pnVirtualKeysManagerClientPGImpl.deleteVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                "VIRTUALKEY_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }

    @Test
    void changeStatusVirtualKey() throws JsonProcessingException {
        RequestVirtualKeyStatus requestVirtualKeyStatus = new RequestVirtualKeyStatus();
        requestVirtualKeyStatus.setStatus(RequestVirtualKeyStatus.StatusEnum.BLOCK);
        String request = objectMapper.writeValueAsString(requestVirtualKeyStatus);

        mockServerClient.when(request().withMethod("PUT")
                        .withPath(path + "/VIRTUALKEY_ID/status")
                        .withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                );

        StepVerifier.create(pnVirtualKeysManagerClientPGImpl.changeStatusVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                "VIRTUALKEY_ID",
                requestVirtualKeyStatus,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext().verifyComplete();
    }

    @Test
    void changeStatusVirtualKeyError() throws JsonProcessingException {

        RequestVirtualKeyStatus requestVirtualKeyStatus = new RequestVirtualKeyStatus();
        requestVirtualKeyStatus.setStatus(RequestVirtualKeyStatus.StatusEnum.BLOCK);
        String request = objectMapper.writeValueAsString(requestVirtualKeyStatus);

        mockServerClient.when(request().withMethod("PUT")
                        .withPath(path + "/VIRTUALKEY_ID/status")
                        .withBody(request))
                .respond(response()
                        .withStatusCode(404)
                );

        StepVerifier.create(pnVirtualKeysManagerClientPGImpl.changeStatusVirtualKey(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet.PG,
                UserMock.PN_CX_ID,
                "VIRTUALKEY_ID",
                requestVirtualKeyStatus,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }
}