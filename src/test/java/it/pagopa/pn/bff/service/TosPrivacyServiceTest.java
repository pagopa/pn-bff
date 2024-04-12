package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyBody;
import it.pagopa.pn.bff.mappers.tosprivacy.TosPrivacyConsentMapper;
import it.pagopa.pn.bff.mocks.ConsentsMock;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {TosPrivacyService.class})
class TosPrivacyServiceTest {
    @Autowired
    private TosPrivacyService tosPrivacyService;
    private PnUserAttributesClientImpl pnUserAttributesClient;
    TosPrivacyConsentMapper modelMapperMock = mock(TosPrivacyConsentMapper.class);
    ConsentsMock consentsMock = new ConsentsMock();

    @BeforeEach
    void setup() {
        this.pnUserAttributesClient = mock(PnUserAttributesClientImpl.class);

        this.tosPrivacyService = new TosPrivacyService(pnUserAttributesClient);
    }

    @Test
    void testGetTosContent() {
        Consent tosConsent = consentsMock.getTosConsentResponseMock();
        Consent privacyConsent = consentsMock.getPrivacyConsentResponseMock();

        when(pnUserAttributesClient.getTosConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class)
        )).thenReturn(Mono.just(tosConsent));

        when(pnUserAttributesClient.getPrivacyConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class)
        )).thenReturn(Mono.just(privacyConsent));

        StepVerifier.create(tosPrivacyService.getTosPrivacy(
                        "UID",
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF
                ))
                .expectNext(consentsMock.getBffTosPrivacyConsentMock())
                .verifyComplete();
    }

    @Test
    void testGetTosContentError() {
        when(pnUserAttributesClient.getTosConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        when(pnUserAttributesClient.getPrivacyConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(tosPrivacyService.getTosPrivacy(
                        "UID",
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void acceptOrDeclineTosPrivacyTest() {
        BffTosPrivacyBody tosPrivacyBody = consentsMock.acceptTosPrivacyBodyMock();

        when(pnUserAttributesClient.acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        Mono<Void> result = tosPrivacyService.acceptOrDeclineTosPrivacy(
                "UID",
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                Mono.just(tosPrivacyBody)
        );

        StepVerifier.create(result).expectComplete().verify();
    }

    @Test
    void acceptOnlyTosTest() {
        BffTosPrivacyBody tosPrivacyBody = consentsMock.acceptTosPrivacyBodyMock().privacy(null);

        when(pnUserAttributesClient.acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        Mono<Void> result = tosPrivacyService.acceptOrDeclineTosPrivacy(
                "UID",
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                Mono.just(tosPrivacyBody)
        );

        StepVerifier.create(result).expectComplete().verify();

    }

    @Test
    void acceptOrDeclineTosPrivacyEmptyBodyErrorTest() {
        when(pnUserAttributesClient.acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(400, "Bad Request", null, null, null)));

        StepVerifier.create(tosPrivacyService.acceptOrDeclineTosPrivacy(
                        "UID",
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        Mono.just(new BffTosPrivacyBody())
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400
                        && ((PnBffException) throwable).getProblem().getDetail().equals("Missing tos or privacy body")
                )
                .verify();
    }
}