package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.ConsentsMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.TosPrivacyService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import it.pagopa.pn.bff.utils.helpers.FluxMatcher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@WebFluxTest(TosPrivacyController.class)
class TosPrivacyControllerTest {
    public static final CxTypeAuthFleet CX_TYPE = CxTypeAuthFleet.PF;
    @Autowired
    WebTestClient webTestClient;
    ConsentsMock consentsMock = new ConsentsMock();
    @MockBean
    private TosPrivacyService tosPrivacyService;

    @Test
    void getTosPrivacy() {
        BffTosPrivacyConsent response = consentsMock.getBffTosPrivacyConsentMock();

        Mockito.when(tosPrivacyService.getTosPrivacy(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class)
                ))
                .thenReturn(Mono.just(response));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.TOS_PRIVACY_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CX_TYPE.toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffTosPrivacyConsent.class)
                .isEqualTo(response);

        Mockito.verify(tosPrivacyService).getTosPrivacy(
                UserMock.PN_UID,
                CX_TYPE
        );
    }

    @Test
    void getTosPrivacyError() {
        Mockito.when(tosPrivacyService.getTosPrivacy(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class)))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.TOS_PRIVACY_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CX_TYPE.toString())
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(tosPrivacyService).getTosPrivacy(
                UserMock.PN_UID,
                CX_TYPE
        );
    }

    @Test
    void acceptTosPrivacy() {
        Mockito.when(tosPrivacyService.acceptOrDeclineTosPrivacy(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.any()))
                .thenReturn(Mono.empty());

        List<BffTosPrivacyActionBody> request = consentsMock.acceptTosPrivacyBodyMock();

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.TOS_PRIVACY_PATH).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CX_TYPE.toString())
                .body(Flux.fromIterable(request), List.class)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);

        Mockito.verify(tosPrivacyService).acceptOrDeclineTosPrivacy(
                eq(UserMock.PN_UID),
                eq(CX_TYPE),
                argThat(new FluxMatcher<>(Flux.fromIterable(request)))
        );
    }

    @Test
    void acceptTosPrivacyError() {
        Mockito.when(tosPrivacyService.acceptOrDeclineTosPrivacy(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.any()))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        List<BffTosPrivacyActionBody> request = consentsMock.acceptTosPrivacyBodyMock();

        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.TOS_PRIVACY_PATH).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CX_TYPE.toString())
                .body(Flux.fromIterable(request), List.class)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(tosPrivacyService).acceptOrDeclineTosPrivacy(
                eq(UserMock.PN_UID),
                eq(CX_TYPE),
                argThat(new FluxMatcher<>(Flux.fromIterable(request)))
        );
    }
}