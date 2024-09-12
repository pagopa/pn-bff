package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody;
import it.pagopa.pn.bff.mocks.ConsentsMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {TosPrivacyService.class})
class TosPrivacyServiceTest {
    private static TosPrivacyService tosPrivacyService;
    private static PnUserAttributesClientImpl pnUserAttributesClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    ConsentsMock consentsMock = new ConsentsMock();

    @BeforeAll
    public static void setup() {
        pnUserAttributesClient = mock(PnUserAttributesClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        tosPrivacyService = new TosPrivacyService(pnUserAttributesClient, pnBffExceptionUtility);
    }

    @Test
    void getConsents() {
        Consent tosConsent = consentsMock.getTosConsentResponseMock();
        Consent privacyConsent = consentsMock.getPrivacyConsentResponseMock();

        when(pnUserAttributesClient.getConsents(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class)
        )).thenReturn(Flux.just(tosConsent, privacyConsent));

        StepVerifier.create(tosPrivacyService.getTosPrivacy(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF
                ))
                .expectNext(consentsMock.getBffTosPrivacyConsentMock())
                .verifyComplete();
    }

    @Test
    void getConsentsError() {
        when(pnUserAttributesClient.getConsents(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class)
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(tosPrivacyService.getTosPrivacy(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void acceptOrDeclineTosPrivacy() {
        List<BffTosPrivacyActionBody> tosPrivacyBody = consentsMock.acceptTosPrivacyBodyMock();

        when(pnUserAttributesClient.acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        Mono<Void> result = tosPrivacyService.acceptOrDeclineTosPrivacy(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                Flux.fromIterable(tosPrivacyBody)
        );

        StepVerifier.create(result).expectComplete().verify();
    }

    @Test
    void acceptOnlyTos() {
        List<BffTosPrivacyActionBody> tosPrivacyBody = consentsMock.acceptTosPrivacyBodyMock();
        tosPrivacyBody.remove(1);

        when(pnUserAttributesClient.acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        )).thenReturn(Mono.empty());

        Mono<Void> result = tosPrivacyService.acceptOrDeclineTosPrivacy(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                Flux.fromIterable(tosPrivacyBody)
        );

        StepVerifier.create(result).expectComplete().verify();

    }

    @Test
    void acceptOrDeclineTosPrivacyError() {
        List<BffTosPrivacyActionBody> tosPrivacyBody = consentsMock.acceptTosPrivacyBodyMock();

        when(pnUserAttributesClient.acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(tosPrivacyService.acceptOrDeclineTosPrivacy(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        Flux.fromIterable(tosPrivacyBody)
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void acceptOrDeclineTosPrivacyWithOneInError() {
        List<BffTosPrivacyActionBody> tosPrivacyBody = consentsMock.acceptTosPrivacyBodyMock();

        when(pnUserAttributesClient.acceptConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.any(ConsentAction.class),
                Mockito.anyString()
        ))
                .thenReturn(Mono.empty())
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));
        
        StepVerifier.create(tosPrivacyService.acceptOrDeclineTosPrivacy(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        Flux.fromIterable(tosPrivacyBody)
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}