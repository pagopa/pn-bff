package it.pagopa.pn.bff.pnclient.userattributes;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnUserAttributesClientImpl.class})
@ExtendWith(SpringExtension.class)
class PnUserAttributesClientImplTest {
    @Autowired
    private PnUserAttributesClientImpl pnUserAttributesClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.api.ConsentsApi")
    private ConsentsApi consentsApi;

    @Test
    void getTosConsent() throws RestClientException {
        when(consentsApi.getConsentByType(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<ConsentType>any(),
                Mockito.isNull()
        )).thenReturn(Mono.just(mock(Consent.class)));

        StepVerifier.create(pnUserAttributesClient.getTosConsent(
                "UID",
                CxTypeAuthFleet.PF
        )).expectNextCount(1).verifyComplete();
    }

    @Test
    void getTosContentError() {
        when(consentsApi.getConsentByType(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<ConsentType>any(),
                Mockito.isNull()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnUserAttributesClient.getTosConsent(
                "UID",
                CxTypeAuthFleet.PF
        )).expectError(PnBffException.class).verify();
    }
}