package it.pagopa.pn.bff.pnclient.userattributes;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.AllApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.CourtesyApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.LegalApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.*;
import it.pagopa.pn.bff.mocks.AddressesMock;
import it.pagopa.pn.bff.mocks.ConsentsMock;
import it.pagopa.pn.bff.mocks.UserMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnUserAttributesClientImpl.class})
@ExtendWith(SpringExtension.class)
class PnUserAttributesClientImplTest {
    private final ConsentsMock consentsMock = new ConsentsMock();
    private final AddressesMock addressesMock = new AddressesMock();
    @Autowired
    private PnUserAttributesClientImpl pnUserAttributesClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi")
    private ConsentsApi consentsApi;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.AllApi")
    private AllApi allAddressesApi;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.CourtesyApi")
    private CourtesyApi courtesyApi;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.LegalApi")
    private LegalApi legalApi;


    @Test
    void getPgConsentByType() throws RestClientException {
        Consent consent = consentsMock.getPgTosConsentResponseMock();
        when(consentsApi.getPgConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any()
        )).thenReturn(Mono.just(consent));

        StepVerifier.create(pnUserAttributesClient.getPgConsentByType(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PG,
                ConsentType.TOS_DEST_B2B
        )).expectNext(consent).verifyComplete();
    }

    @Test
    void getPgContentByTypeError() {
        when(consentsApi.getPgConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.getPgConsentByType(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PG,
                ConsentType.TOS_DEST_B2B
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getConsentByType() throws RestClientException {
        Consent consent = consentsMock.getTosConsentResponseMock();
        when(consentsApi.getConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any()
        )).thenReturn(Mono.just(consent));

        StepVerifier.create(pnUserAttributesClient.getConsentByType(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                ConsentType.TOS
        )).expectNext(consent).verifyComplete();
    }

    @Test
    void getContentByTypeError() {
        when(consentsApi.getConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.getConsentByType(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                ConsentType.TOS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void acceptConsentPg() {
        when(consentsApi.setPgConsentAction(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(ConsentAction.class),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        StepVerifier.create(pnUserAttributesClient.acceptConsentPg(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PG,
                ConsentType.TOS_DEST_B2B,
                UserMock.PN_CX_ROLE,
                "1",
                new ConsentAction().action(ConsentAction.ActionEnum.ACCEPT),
                UserMock.PN_CX_GROUPS
        )).verifyComplete();
    }

    @Test
    void acceptConsentPgError() {
        when(consentsApi.setPgConsentAction(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(ConsentAction.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.acceptConsentPg(
                UserMock.PN_CX_ID,
                CxTypeAuthFleet.PG,
                ConsentType.TOS_DEST_B2B,
                UserMock.PN_CX_ROLE,
                "1",
                new ConsentAction().action(ConsentAction.ActionEnum.ACCEPT),
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void acceptConsent() {
        when(consentsApi.consentAction(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.anyString(),
                Mockito.any(ConsentAction.class)
        )).thenReturn(Mono.empty());

        StepVerifier.create(pnUserAttributesClient.acceptConsent(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                ConsentType.TOS,
                new ConsentAction().action(ConsentAction.ActionEnum.ACCEPT),
                "1"
        )).verifyComplete();
    }

    @Test
    void acceptConsentError() {
        when(consentsApi.consentAction(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.anyString(),
                Mockito.any(ConsentAction.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.acceptConsent(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                ConsentType.TOS,
                new ConsentAction().action(ConsentAction.ActionEnum.ACCEPT),
                "1"
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getUserAddresses() {
        when(allAddressesApi.getAddressesByRecipient(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(addressesMock.getUserAddressesResponseMock()));

        StepVerifier.create(pnUserAttributesClient.getUserAddresses(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext(addressesMock.getUserAddressesResponseMock()).verifyComplete();
    }

    @Test
    void getUserAddressesError() {
        when(allAddressesApi.getAddressesByRecipient(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.getUserAddresses(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void createOrUpdateCourtesyAddress() {
        when(courtesyApi.postRecipientCourtesyAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(addressesMock.addressVerificationCourtesyResponseMock()));

        StepVerifier.create(pnUserAttributesClient.createOrUpdateCourtesyAddress(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                CourtesyChannelType.EMAIL,
                addressesMock.getAddressVerificationBodyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext(addressesMock.addressVerificationCourtesyResponseMock()).verifyComplete();
    }

    @Test
    void createOrUpdateCourtesyAddressError() {
        when(courtesyApi.postRecipientCourtesyAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.createOrUpdateCourtesyAddress(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                CourtesyChannelType.EMAIL,
                addressesMock.getAddressVerificationBodyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void createOrUpdateLegalAddress() {
        when(legalApi.postRecipientLegalAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.just(addressesMock.addressVerificationCourtesyResponseMock()));

        StepVerifier.create(pnUserAttributesClient.createOrUpdateLegalAddress(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                LegalChannelType.PEC,
                addressesMock.getAddressVerificationBodyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectNext(addressesMock.addressVerificationCourtesyResponseMock()).verifyComplete();
    }

    @Test
    void createOrUpdateLegalAddressError() {
        when(legalApi.postRecipientLegalAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.createOrUpdateLegalAddress(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                LegalChannelType.PEC,
                addressesMock.getAddressVerificationBodyMock(),
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void deleteCourtesyAddress() {
        when(courtesyApi.deleteRecipientCourtesyAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        StepVerifier.create(pnUserAttributesClient.deleteRecipientCourtesyAddress(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                CourtesyChannelType.EMAIL,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).verifyComplete();
    }

    @Test
    void deleteCourtesyAddressError() {
        when(courtesyApi.deleteRecipientCourtesyAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.deleteRecipientCourtesyAddress(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                CourtesyChannelType.EMAIL,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void deleteLegalAddress() {
        when(legalApi.deleteRecipientLegalAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        StepVerifier.create(pnUserAttributesClient.deleteRecipientLegalAddress(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                LegalChannelType.PEC,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).verifyComplete();
    }

    @Test
    void deleteLegalAddressError() {
        when(legalApi.deleteRecipientLegalAddress(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.deleteRecipientLegalAddress(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                LegalChannelType.PEC,
                UserMock.PN_CX_GROUPS,
                UserMock.PN_CX_ROLE
        )).expectError(WebClientResponseException.class).verify();
    }
}