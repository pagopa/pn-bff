package it.pagopa.pn.bff.pnclient.externalregistries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoV21;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import it.pagopa.pn.bff.mocks.PaymentsMock;
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

import java.util.List;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PnExternalRegistriesClientImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String pathInstitutions = "/ext-registry/pa/v1/institutions";
    private final String pathGroups = "/ext-registry/pa/v1/groups";
    private final String pathPaymentInfo = "/ext-registry/pagopa/v2.1/paymentinfo";
    private final UserMock userMock = new UserMock();
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
    private final PaymentsMock paymentsMock = new PaymentsMock();
    @Autowired
    private PnExternalRegistriesClientImpl pnExternalRegistriesClient;

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

        StepVerifier.create(pnExternalRegistriesClient.getInstitutions(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectNextSequence(institutionAndProductMock.getInstitutionResourcePNMock()).verifyComplete();
    }

    @Test
    void getInstitutionsError() {
        mockServerClient.when(request().withMethod("GET").withPath(pathInstitutions))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnExternalRegistriesClient.getInstitutions(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void getInstitutionProducts() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(institutionAndProductMock.getProductResourcePNMock());
        mockServerClient.when(request().withMethod("GET").withPath(pathInstitutions + "/CX_ID/products"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnExternalRegistriesClient.getInstitutionProducts(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectNextSequence(institutionAndProductMock.getProductResourcePNMock()).verifyComplete();
    }

    @Test
    void getInstitutionProductsError() {
        mockServerClient.when(request().withMethod("GET").withPath(pathInstitutions + "/CX_ID/products"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnExternalRegistriesClient.getInstitutionProducts(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
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

        StepVerifier.create(pnExternalRegistriesClient.getGroups(
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

        StepVerifier.create(pnExternalRegistriesClient.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                PaGroupStatus.ACTIVE
        )).expectError().verify();
    }

    @Test
    void getPaymentsInfo() throws JsonProcessingException {
        List<PaymentInfoRequest> paymentsInfoRequest = paymentsMock.getPaymentsInfoRequestMock();
        List<PaymentInfoV21> paymentsInfoResponse = paymentsMock.getPaymentsInfoResponseMock();
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(paymentsInfoRequest);
        String response = objectMapper.writeValueAsString(paymentsInfoResponse);

        mockServerClient.when(request().withMethod("POST").withPath(pathPaymentInfo).withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnExternalRegistriesClient.getPaymentsInfo(
                paymentsMock.getPaymentsInfoRequestMock()
        )).expectNextSequence(paymentsInfoResponse).verifyComplete();
    }

    @Test
    void getPaymentsInfoError() throws JsonProcessingException {
        List<PaymentInfoRequest> paymentsInfoRequest = paymentsMock.getPaymentsInfoRequestMock();
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(paymentsInfoRequest);

        mockServerClient.when(request().withMethod("POST").withPath(pathPaymentInfo).withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnExternalRegistriesClient.getPaymentsInfo(
                paymentsMock.getPaymentsInfoRequestMock()
        )).expectError().verify();
    }
}