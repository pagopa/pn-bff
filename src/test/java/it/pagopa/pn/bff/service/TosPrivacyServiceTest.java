package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyBody;
import it.pagopa.pn.bff.mocks.ConsentsMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

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
    void getTosContent() {
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
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF
                ))
                .expectNext(consentsMock.getBffTosPrivacyConsentMock())
                .verifyComplete();
    }

    @Test
    void getTosContentError() {
        when(pnUserAttributesClient.getTosConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        when(pnUserAttributesClient.getPrivacyConsent(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

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
        BffTosPrivacyBody tosPrivacyBody = consentsMock.acceptTosPrivacyBodyMock();

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
                Mono.just(tosPrivacyBody)
        );

        StepVerifier.create(result).expectComplete().verify();
    }

    @Test
    void acceptOnlyTos() {
        BffTosPrivacyBody tosPrivacyBody = consentsMock.acceptTosPrivacyBodyMock().privacy(null);

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
                Mono.just(tosPrivacyBody)
        );

        StepVerifier.create(result).expectComplete().verify();

    }

    @Test
    void acceptOrDeclineTosPrivacyEmptyBodyError() {
        StepVerifier.create(tosPrivacyService.acceptOrDeclineTosPrivacy(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PF,
                        Mono.just(new BffTosPrivacyBody())
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400
                        && Objects.equals(((PnBffException) throwable).getProblem().getType(), "GENERIC_ERROR")
                        && ((PnBffException) throwable).getProblem().getDetail().equals("The body of the request is missed")
                )
                .verify();
    }
}