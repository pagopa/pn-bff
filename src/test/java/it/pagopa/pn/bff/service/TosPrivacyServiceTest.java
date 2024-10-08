package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyActionBody;
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

import java.util.ArrayList;
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
    void getPgConsents() {
        Consent tosConsent = consentsMock.getPgTosConsentResponseMock();
        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType> type = new ArrayList<>();
        type.add(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType.TOS_DEST_B2B);

        when(pnUserAttributesClient.getPgConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class)
        ))
                .thenReturn(Mono.just(tosConsent));

        StepVerifier.create(tosPrivacyService.getPgTosPrivacy(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PG,
                        type
                ))
                .expectNextSequence(consentsMock.getBffPgTosPrivacyConsentMock())
                .verifyComplete();
    }

    @Test
    void getPgConsentsError() {
        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType> type = new ArrayList<>();
        type.add(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType.TOS_DEST_B2B);

        when(pnUserAttributesClient.getPgConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(tosPrivacyService.getPgTosPrivacy(
                        UserMock.PN_CX_ID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PG,
                        type
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void acceptOrDeclinePgTosPrivacy() {
        List<BffTosPrivacyActionBody> tosPrivacyBody = consentsMock.acceptPgTosPrivacyBodyMock();

        when(pnUserAttributesClient.acceptConsentPg(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(ConsentAction.class),
                Mockito.anyList()
        )).thenReturn(Mono.empty());

        Mono<Void> result = tosPrivacyService.acceptOrDeclinePgTosPrivacy(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PG,
                Flux.fromIterable(tosPrivacyBody),
                UserMock.PN_CX_ROLE,
                UserMock.PN_CX_GROUPS
                );

        StepVerifier.create(result).expectComplete().verify();
    }

    @Test
    void acceptOrDeclinePGTosPrivacyError() {
        List<BffTosPrivacyActionBody> tosPrivacyBody = consentsMock.acceptPgTosPrivacyBodyMock();

        when(pnUserAttributesClient.acceptConsentPg(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(ConsentAction.class),
                Mockito.anyList()
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(tosPrivacyService.acceptOrDeclinePgTosPrivacy(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PG,
                        Flux.fromIterable(tosPrivacyBody),
                        UserMock.PN_CX_ROLE,
                        UserMock.PN_CX_GROUPS

                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getConsents() {
        Consent tosConsent = consentsMock.getTosConsentResponseMock();
        Consent privacyConsent = consentsMock.getPrivacyConsentResponseMock();
        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType> type = new ArrayList<>();
        type.add(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType.TOS);
        type.add(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType.DATAPRIVACY);

        when(pnUserAttributesClient.getConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class)
        ))
                .thenReturn(Mono.just(tosConsent))
                .thenReturn(Mono.just(privacyConsent));

        StepVerifier.create(tosPrivacyService.getTosPrivacy(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        type
                ))
                .expectNextSequence(consentsMock.getBffTosPrivacyConsentMock())
                .verifyComplete();
    }

    @Test
    void getOnlyTosConsent() {
        Consent tosConsent = consentsMock.getTosConsentResponseMock();
        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType> type = new ArrayList<>();
        type.add(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType.TOS);

        when(pnUserAttributesClient.getConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class)
        )).thenReturn(Mono.just(tosConsent));

        StepVerifier.create(tosPrivacyService.getTosPrivacy(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        type
                ))
                .expectNextSequence(consentsMock.getBffTosPrivacyConsentMock().subList(0, 1))
                .verifyComplete();
    }

    @Test
    void getConsentsError() {
        List<it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType> type = new ArrayList<>();
        type.add(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType.TOS);
        type.add(it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.ConsentType.DATAPRIVACY);

        when(pnUserAttributesClient.getConsentByType(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.any(ConsentType.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(tosPrivacyService.getTosPrivacy(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        type
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
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
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
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
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
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
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PF,
                        Flux.fromIterable(tosPrivacyBody)
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}