package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.mappers.tosprivacy.TosPrivacyMapper;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {TosPrivacyService.class})
class TosPrivacyServiceTest {
    @Autowired
    private TosPrivacyService tosPrivacyService;
    private PnUserAttributesClientImpl pnUserAttributesClient;
    TosPrivacyMapper modelMapperMock = mock(TosPrivacyMapper.class);

    @BeforeEach
    void setup() {
        this.pnUserAttributesClient = mock(PnUserAttributesClientImpl.class);

        this.tosPrivacyService = new TosPrivacyService(pnUserAttributesClient);
    }

    @Test
    void testGetTosContent() {
        when(pnUserAttributesClient.getTosConsent(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any()
        )).thenReturn(Mono.just(new Consent()));

        when(pnUserAttributesClient.getPrivacyConsent(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any()
        )).thenReturn(Mono.just(new Consent()));

        when(modelMapperMock.mapTosPrivacyConsent(any(Consent.class)))
                .thenReturn(new BffConsent());

        tosPrivacyService.getTosPrivacy(
                "UID",
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF
        );

        Mockito.verify(pnUserAttributesClient).getTosConsent(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any()
        );
    }

    @Test
    void testGetNotificationDetailIunNotFound() {
        when(pnUserAttributesClient.getTosConsent(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        when(pnUserAttributesClient.getPrivacyConsent(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(tosPrivacyService.getTosPrivacy(
                        "UID",
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}