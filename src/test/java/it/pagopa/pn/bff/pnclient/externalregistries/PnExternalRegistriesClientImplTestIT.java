package it.pagopa.pn.bff.pnclient.externalregistries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
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

import java.util.List;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PnExternalRegistriesClientImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String path = "/ext-registry/pa/v1/institutions";
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
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
        String response = objectMapper.writeValueAsString(institutionAndProductMock.getInstitutionResourcePNSMock());
        mockServerClient.when(request().withMethod("GET").withPath(path))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnInfoPaClient.getInstitutions(
                "xPagopaPnUid",
                CxTypeAuthFleet.PA,
                "xPagopaPnCxId",
                "xPagopaPnSrcCh",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnSrcChDetails"
        )).expectNextSequence(institutionAndProductMock.getInstitutionResourcePNSMock()).verifyComplete();
    }

    @Test
    void getInstitutionsError() {
        mockServerClient.when(request().withMethod("GET").withPath(path))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnInfoPaClient.getInstitutions(
                "xPagopaPnUid",
                CxTypeAuthFleet.PA,
                "xPagopaPnCxId",
                "xPagopaPnSrcCh",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnSrcChDetails"
        )).expectError().verify();
    }

    @Test
    void getInstitutionProducts() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(institutionAndProductMock.getProductResourcePNSMock());
        mockServerClient.when(request().withMethod("GET").withPath(path + "/xPagopaPnSrcChDetails/products"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnInfoPaClient.getInstitutionProduct(
                "xPagopaPnUid",
                CxTypeAuthFleet.PA,
                "xPagopaPnCxId",
                "xPagopaPnSrcCh",
                "xPagopaPnSrcChDetails",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnSrcChDetails"
        )).expectNextSequence(institutionAndProductMock.getProductResourcePNSMock()).verifyComplete();
    }

    @Test
    void getInstitutionProductsError() {
        mockServerClient.when(request().withMethod("GET").withPath(path + "/xPagopaPnSrcChDetails/products"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnInfoPaClient.getInstitutionProduct(
                "xPagopaPnUid",
                CxTypeAuthFleet.PA,
                "xPagopaPnCxId",
                "xPagopaPnSrcCh",
                "xPagopaPnSrcChDetails",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnSrcChDetails"
        )).expectError().verify();
    }
}