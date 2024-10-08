package it.pagopa.pn.bff.pnclient.userattributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.*;
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
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String path = "/user-consents/v1/consents";
    private final String pathPG = "/pg-consents/v1/consents/";
    private final String addressPath = "/address-book/v1/digital-address";
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
    void getPgConsentByType() throws JsonProcessingException {
        Consent consent = consentsMock.getPgTosConsentResponseMock();
        String response = objectMapper.writeValueAsString(consent);
        mockServerClient.when(request().withMethod("GET").withPath(pathPG +  ConsentType.TOS_DEST_B2B))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnUserAttributesClient.getPgConsentByType(
                UserMock.PN_CX_ID,
                CX_TYPE,
                ConsentType.TOS_DEST_B2B
        )).expectNext(consent).verifyComplete();
    }

    @Test
    void getPgConsentByTypeError() {
        mockServerClient.when(request().withMethod("GET").withPath(pathPG +  ConsentType.TOS_DEST_B2B))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.getPgConsentByType(
                UserMock.PN_CX_ID,
                CX_TYPE,
                ConsentType.TOS_DEST_B2B
        )).expectError().verify();
    }

    @Test
    void acceptPgTosConsent() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(consentsMock.requestConsentActionMock());
        mockServerClient.when(request().withMethod("PUT").withPath(pathPG +  ConsentType.TOS_DEST_B2B).withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody("")
                );

        StepVerifier.create(pnUserAttributesClient.acceptConsentPg(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PG,
                ConsentType.TOS_DEST_B2B,
                UserMock.PN_CX_ROLE,
                "1",
                new ConsentAction().action(ConsentAction.ActionEnum.ACCEPT),
                UserMock.PN_CX_GROUPS
        )).expectNext().verifyComplete();
    }

    @Test
    void acceptPgTosConsentError() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(consentsMock.requestConsentActionMock());
        mockServerClient.when(request().withMethod("PUT").withPath(pathPG +  ConsentType.TOS_DEST_B2B).withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.acceptConsentPg(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PG,
                ConsentType.TOS_DEST_B2B,
                UserMock.PN_CX_ROLE,
                "1",
                new ConsentAction().action(ConsentAction.ActionEnum.ACCEPT),
                UserMock.PN_CX_GROUPS
        )).expectError().verify();
    }

    @Test
    void getConsentByType() throws JsonProcessingException {
        Consent consent = consentsMock.getTosConsentResponseMock();
        String response = objectMapper.writeValueAsString(consent);
        mockServerClient.when(request().withMethod("GET").withPath(path + "/TOS"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnUserAttributesClient.getConsentByType(
                UserMock.PN_UID,
                CX_TYPE,
                ConsentType.TOS
        )).expectNext(consent).verifyComplete();
    }

    @Test
    void getConsentByTypeError() {
        mockServerClient.when(request().withMethod("GET").withPath(path + "/TOS"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.getConsentByType(
                UserMock.PN_UID,
                CX_TYPE,
                ConsentType.TOS
        )).expectError().verify();
    }

    @Test
    void acceptTosConsent() throws JsonProcessingException {
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
        String response = objectMapper.writeValueAsString(addressesMock.getUserAddressesResponseMock());
        mockServerClient.when(request().withMethod("GET").withPath(addressPath))
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
        mockServerClient.when(request().withMethod("GET").withPath(addressPath))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.getUserAddresses(
                UserMock.PN_UID,
                CX_TYPE,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }

    @Test
    void createOrUpdateCourtesyAddress() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(addressesMock.getAddressVerificationBodyMock());
        String response = objectMapper.writeValueAsString(addressesMock.addressVerificationCourtesyResponseMock());
        mockServerClient.when(request().withMethod("POST").withPath(addressPath + "/courtesy/default/EMAIL").withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnUserAttributesClient.createOrUpdateCourtesyAddress(
                UserMock.PN_UID,
                CX_TYPE,
                AddressesMock.SENDER_ID,
                CourtesyChannelType.EMAIL,
                addressesMock.getAddressVerificationBodyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext(addressesMock.addressVerificationCourtesyResponseMock()).verifyComplete();
    }

    @Test
    void createOrUpdateCourtesyAddressError() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(addressesMock.getAddressVerificationBodyMock());
        mockServerClient.when(request().withMethod("POST").withPath(addressPath + "/courtesy/default/EMAIL").withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.createOrUpdateCourtesyAddress(
                UserMock.PN_UID,
                CX_TYPE,
                AddressesMock.SENDER_ID,
                CourtesyChannelType.EMAIL,
                addressesMock.getAddressVerificationBodyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }

    @Test
    void createOrUpdateLegalAddress() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(addressesMock.getAddressVerificationBodyMock());
        String response = objectMapper.writeValueAsString(addressesMock.addressVerificationCourtesyResponseMock());
        mockServerClient.when(request().withMethod("POST").withPath(addressPath + "/legal/default/PEC").withBody(request))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnUserAttributesClient.createOrUpdateLegalAddress(
                UserMock.PN_UID,
                CX_TYPE,
                AddressesMock.SENDER_ID,
                LegalChannelType.PEC,
                addressesMock.getAddressVerificationBodyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext(addressesMock.addressVerificationCourtesyResponseMock()).verifyComplete();
    }

    @Test
    void createOrUpdateLegalAddressError() throws JsonProcessingException {
        String request = objectMapper.writeValueAsString(addressesMock.getAddressVerificationBodyMock());
        mockServerClient.when(request().withMethod("POST").withPath(addressPath + "/legal/default/PEC").withBody(request))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.createOrUpdateLegalAddress(
                UserMock.PN_UID,
                CX_TYPE,
                AddressesMock.SENDER_ID,
                LegalChannelType.PEC,
                addressesMock.getAddressVerificationBodyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }

    @Test
    void deleteCourtesyAddress() {
        mockServerClient.when(request().withMethod("DELETE").withPath(addressPath + "/courtesy/default/EMAIL"))
                .respond(response().withStatusCode(200));

        StepVerifier.create(pnUserAttributesClient.deleteRecipientCourtesyAddress(
                UserMock.PN_UID,
                CX_TYPE,
                AddressesMock.SENDER_ID,
                CourtesyChannelType.EMAIL,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext().verifyComplete();
    }

    @Test
    void deleteCourtesyAddressError() {
        mockServerClient.when(request().withMethod("DELETE").withPath(addressPath + "/courtesy/default/EMAIL"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.deleteRecipientCourtesyAddress(
                UserMock.PN_UID,
                CX_TYPE,
                AddressesMock.SENDER_ID,
                CourtesyChannelType.EMAIL,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }

    @Test
    void deleteLegalAddress() {
        mockServerClient.when(request().withMethod("DELETE").withPath(addressPath + "/legal/default/PEC"))
                .respond(response().withStatusCode(200));

        StepVerifier.create(pnUserAttributesClient.deleteRecipientLegalAddress(
                UserMock.PN_UID,
                CX_TYPE,
                AddressesMock.SENDER_ID,
                LegalChannelType.PEC,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext().verifyComplete();
    }

    @Test
    void deleteLegalAddressError() {
        mockServerClient.when(request().withMethod("DELETE").withPath(addressPath + "/legal/default/PEC"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnUserAttributesClient.deleteRecipientLegalAddress(
                UserMock.PN_UID,
                CX_TYPE,
                AddressesMock.SENDER_ID,
                LegalChannelType.PEC,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError().verify();
    }
}