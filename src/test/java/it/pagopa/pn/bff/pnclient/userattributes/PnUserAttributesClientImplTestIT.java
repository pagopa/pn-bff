package it.pagopa.pn.bff.pnclient.userattributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.AddressesMock;
import it.pagopa.pn.bff.mocks.ConsentsMock;
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
class PnUserAttributesClientImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String path = "/user-consents/v1/consents";
    private final ConsentsMock consentsMock = new ConsentsMock();
    private final AddressesMock addressesMock = new AddressesMock();
    private final CxTypeAuthFleet CX_TYPE = CxTypeAuthFleet.PF;
    @Autowired
    private PnUserAttributesClientImpl pnUserAttributesClient;

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
    void getTosConsent() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(consentsMock.getTosConsentResponseMock());
        mockServerClient.when(request().withMethod("GET").withPath(path + "/TOS"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnUserAttributesClient.getTosConsent(
                UserMock.PN_UID,
                CX_TYPE
        )).expectNext(consentsMock.getTosConsentResponseMock()).verifyComplete();
    }

    @Test
    void getTosConsentError() {
        mockServerClient.when(request().withMethod("GET").withPath(path + "/TOS"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.getTosConsent(
                UserMock.PN_UID,
                CX_TYPE
        )).expectError().verify();
    }

    @Test
    void getPrivacyConsent() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(consentsMock.getPrivacyConsentResponseMock());
        mockServerClient.when(request().withMethod("GET").withPath(path + "/DATAPRIVACY"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnUserAttributesClient.getPrivacyConsent(
                UserMock.PN_UID,
                CX_TYPE
        )).expectNext(consentsMock.getPrivacyConsentResponseMock()).verifyComplete();
    }

    @Test
    void getPrivacyConsentError() {
        mockServerClient.when(request().withMethod("GET").withPath(path + "/DATAPRIVACY"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.getPrivacyConsent(
                UserMock.PN_UID,
                CX_TYPE
        )).expectError().verify();
    }

    @Test
    void acceptTosConsent() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(consentsMock.requestConsentActionMock());
        mockServerClient.when(request().withMethod("PUT").withPath(path + "/TOS").withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody("")
                );

        StepVerifier.create(pnUserAttributesClient.acceptConsent(
                UserMock.PN_UID,
                CX_TYPE,
                ConsentType.TOS,
                new ConsentAction().action(ConsentAction.ActionEnum.ACCEPT),
                "1.0"
        )).expectNext().verifyComplete();
    }

    @Test
    void acceptTosConsentError() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(consentsMock.requestConsentActionMock());
        mockServerClient.when(request().withMethod("PUT").withPath(path + "/TOS").withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.acceptConsent(
                UserMock.PN_UID,
                CX_TYPE,
                ConsentType.TOS,
                new ConsentAction().action(ConsentAction.ActionEnum.ACCEPT),
                "1.0"
        )).expectError().verify();
    }

    @Test
    void getAddressesByRecipient() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(addressesMock.getUserAddressesResponseMock());
        mockServerClient.when(request().withMethod("GET").withPath("/address-book/v1/digital-address"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnUserAttributesClient.getUserAddresses(
                UserMock.PN_UID,
                CX_TYPE,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext(addressesMock.getUserAddressesResponseMock()).verifyComplete();
    }

    @Test
    void getAddressesByRecipientError() {
        mockServerClient.when(request().withMethod("GET").withPath("/address-book/v1/digital-address"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.getUserAddresses(
                UserMock.PN_UID,
                CX_TYPE,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }
}