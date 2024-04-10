package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyBody;
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
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class)
        )).thenReturn(Mono.just(new Consent()));

        when(pnUserAttributesClient.getPrivacyConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class)
        )).thenReturn(Mono.just(new Consent()));

        when(modelMapperMock.mapTosPrivacyConsent(any(Consent.class)))
                .thenReturn(new BffConsent());

        StepVerifier.create(tosPrivacyService.getTosPrivacy(
                        "UID",
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF
                ))
                .expectNextCount(1)
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
        BffTosPrivacyBody tosPrivacyBody = new BffTosPrivacyBody()
                .privacy(new BffTosPrivacyActionBody().action(BffTosPrivacyActionBody.ActionEnum.ACCEPT).version("1"))
                .tos(new BffTosPrivacyActionBody().action(BffTosPrivacyActionBody.ActionEnum.ACCEPT).version("1"));


        when(pnUserAttributesClient.acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        StepVerifier.create(tosPrivacyService.acceptOrDeclineTosPrivacy(
                        "UID",
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        Mono.just(tosPrivacyBody)
                ))
                .expectComplete()
                .verify();

        // Check that client is called twice (once for privacy and once for tos)
        Mockito.verify(pnUserAttributesClient, Mockito.times(2)).acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        );
    }

    @Test
    void acceptOnlyTosTest() {
        BffTosPrivacyBody tosPrivacyBody = new BffTosPrivacyBody()
                .tos(new BffTosPrivacyActionBody().action(BffTosPrivacyActionBody.ActionEnum.ACCEPT).version("1"));

        when(pnUserAttributesClient.acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        StepVerifier.create(tosPrivacyService.acceptOrDeclineTosPrivacy(
                        "UID",
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        Mono.just(tosPrivacyBody)
                ))
                .expectComplete()
                .verify();

        // Check that client is called once
        Mockito.verify(pnUserAttributesClient, Mockito.times(1)).acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        );
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