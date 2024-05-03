package it.pagopa.pn.bff.pnclient.userattributes;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
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
    @Autowired
    private PnUserAttributesClientImpl pnUserAttributesClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi")
    private ConsentsApi consentsApi;

    @Test
    void getTosConsent() throws RestClientException {
        when(consentsApi.getConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.isNull()
        )).thenReturn(Mono.just(consentsMock.getTosConsentResponseMock()));

        StepVerifier.create(pnUserAttributesClient.getTosConsent(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF
        )).expectNext(consentsMock.getTosConsentResponseMock()).verifyComplete();
    }

    @Test
    void getTosContentError() {
        when(consentsApi.getConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.isNull()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.getTosConsent(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getPrivacyConsent() throws RestClientException {
        when(consentsApi.getConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.isNull()
        )).thenReturn(Mono.just(consentsMock.getPrivacyConsentResponseMock()));

        StepVerifier.create(pnUserAttributesClient.getTosConsent(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF
        )).expectNext(consentsMock.getPrivacyConsentResponseMock()).verifyComplete();
    }

    @Test
    void getPrivacyContentError() {
        when(consentsApi.getConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.isNull()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.getTosConsent(
                UserMock.PN_UID,
                CxTypeAuthFleet.PF
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
}