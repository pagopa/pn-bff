package it.pagopa.pn.bff.pnclient.apikeys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestApiKeyStatus;
import it.pagopa.pn.bff.mocks.ApiKeysMock;
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
class PnApikeyManagerClientPAImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String path = "/api-key-self/api-keys";
    private final ApiKeysMock apiKeysMock = new ApiKeysMock();
    @Autowired
    private PnApikeyManagerClientPAImpl paApikeyManagerClient;

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
    void getApiKeys() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        String response = objectMapper.writeValueAsString(apiKeysMock.getApiKeysMock());
        mockServerClient.when(request().withMethod("GET").withPath(path))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(paApikeyManagerClient.getApiKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        )).expectNext(apiKeysMock.getApiKeysMock()).verifyComplete();
    }

    @Test
    void getApiKeysError() {
        mockServer.when(request().withMethod("GET").withPath(path))
                .respond(response().withStatusCode(404));

        StepVerifier.create(paApikeyManagerClient.getApiKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                10,
                "LAST_KEY",
                "LAST_UPDATE",
                true
        )).expectError().verify();
    }

    @Test
    void newApiKey() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(apiKeysMock.geRequestNewApiKeyMock());
        String response = objectMapper.writeValueAsString(apiKeysMock.geResponseNewApiKeyMock());
        mockServerClient.when(request().withMethod("POST").withPath(path).withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(paApikeyManagerClient.newApiKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                apiKeysMock.geRequestNewApiKeyMock(),
                UserMock.PN_CX_GROUPS
        )).expectNext(apiKeysMock.geResponseNewApiKeyMock()).verifyComplete();
    }

    @Test
    void newApiKeyError() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(apiKeysMock.geRequestNewApiKeyMock());
        mockServerClient.when(request().withMethod("POST").withPath(path).withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(paApikeyManagerClient.newApiKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                apiKeysMock.geRequestNewApiKeyMock(),
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void deleteApiKeys() {
        mockServerClient.when(request().withMethod("DELETE").withPath(path + "/API_KEY_ID"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                );

        StepVerifier.create(paApikeyManagerClient.deleteApiKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                UserMock.PN_CX_GROUPS
        )).expectNext().verifyComplete();
    }

    @Test
    void deleteApiKeysError() {
        mockServerClient.when(request().withMethod("DELETE").withPath(path + "/API_KEY_ID"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(paApikeyManagerClient.deleteApiKeys(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void changeStatusApiKey() throws JsonProcessingException {
        RequestApiKeyStatus requestApiKeyStatus = new RequestApiKeyStatus();
        requestApiKeyStatus.setStatus(RequestApiKeyStatus.StatusEnum.BLOCK);
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(requestApiKeyStatus);
        mockServerClient.when(request().withMethod("PUT").withPath(path + "/API_KEY_ID/status").withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                );

        StepVerifier.create(paApikeyManagerClient.changeStatusApiKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                requestApiKeyStatus,
                UserMock.PN_CX_GROUPS
        )).expectNext().verifyComplete();
    }

    @Test
    void changeStatusApiKeyError() throws JsonProcessingException {
        RequestApiKeyStatus requestApiKeyStatus = new RequestApiKeyStatus();
        requestApiKeyStatus.setStatus(RequestApiKeyStatus.StatusEnum.BLOCK);
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(requestApiKeyStatus);
        mockServerClient.when(request().withMethod("PUT").withPath(path + "/API_KEY_ID/status").withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(paApikeyManagerClient.changeStatusApiKey(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                "API_KEY_ID",
                requestApiKeyStatus,
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }
}