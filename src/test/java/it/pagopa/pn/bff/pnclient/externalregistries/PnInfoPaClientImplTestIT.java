package it.pagopa.pn.bff.pnclient.externalregistries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
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
public class PnInfoPaClientImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String pathInstitutions = "/ext-registry/pa/v1/institutions";
    private final String pathGroups = "/ext-registry/pa/v1/groups";
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
    private final UserMock userMock = new UserMock();
    @Autowired
    private PnInfoPaClientImpl pnInfoPaClient;

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
    void getInstitutions() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(institutionAndProductMock.getInstitutionResourcePNMock());
        mockServerClient.when(request().withMethod("GET").withPath(pathInstitutions))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnInfoPaClient.getInstitutions(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.SEARCH_CHANNEL,
                UserMock.PN_CX_GROUPS,
                UserMock.SEARCH_DETAILS
        )).expectNextSequence(institutionAndProductMock.getInstitutionResourcePNMock()).verifyComplete();
    }

    @Test
    void getInstitutionsError() {
        mockServerClient.when(request().withMethod("GET").withPath(pathInstitutions))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnInfoPaClient.getInstitutions(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.SEARCH_CHANNEL,
                UserMock.PN_CX_GROUPS,
                UserMock.SEARCH_DETAILS
        )).expectError().verify();
    }

    @Test
    void getInstitutionProducts() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(institutionAndProductMock.getProductResourcePNMock());
        mockServerClient.when(request().withMethod("GET").withPath(pathInstitutions + "/PRODUCT_ID/products"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnInfoPaClient.getInstitutionProduct(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.SEARCH_CHANNEL,
                "PRODUCT_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.SEARCH_DETAILS
        )).expectNextSequence(institutionAndProductMock.getProductResourcePNMock()).verifyComplete();
    }

    @Test
    void getInstitutionProductsError() {
        mockServerClient.when(request().withMethod("GET").withPath(pathInstitutions + "/PRODUCT_ID/products"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnInfoPaClient.getInstitutionProduct(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.SEARCH_CHANNEL,
                "PRODUCT_ID",
                UserMock.PN_CX_GROUPS,
                UserMock.SEARCH_DETAILS
        )).expectError().verify();
    }

    @Test
    void getGroups() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(userMock.getPaGroupsMock());
        mockServerClient.when(request().withMethod("GET").withPath(pathGroups))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnInfoPaClient.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                PaGroupStatus.ACTIVE
        )).expectNextSequence(userMock.getPaGroupsMock()).verifyComplete();

    }

    @Test
    void getGroupsError() {
        mockServerClient.when(request().withMethod("GET").withPath(pathGroups))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnInfoPaClient.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                PaGroupStatus.ACTIVE
        )).expectError().verify();
    }
}