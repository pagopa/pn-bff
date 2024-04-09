package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyActionBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyConsent;
import it.pagopa.pn.bff.service.TosPrivacyService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@Slf4j
@WebFluxTest(TosPrivacyController.class)
public class TosPrivacyControllerTest {
    public static final String UID = "Uid";
    public static final CxTypeAuthFleet CX_TYPE = CxTypeAuthFleet.PF;
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private TosPrivacyService tosPrivacyService;
    @SpyBean
    private TosPrivacyController tosPrivacyController;

    @Test
    void getTosPrivacy() {
        Mockito.when(tosPrivacyService.getTosPrivacy(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any()
                ))
                .thenReturn(Mono.just(new TosPrivacyConsent()));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.TOS_PRIVACY_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CX_TYPE.toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TosPrivacyConsent.class);

        Mockito.verify(tosPrivacyService).getTosPrivacy(
                UID,
                CX_TYPE
        );
    }

    @Test
    void getTosPrivacyError() {
        Mockito.doThrow(new PnBffException("Err", "Err", "Err", 404, "Err", null))
                .when(tosPrivacyService).getTosPrivacy(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any()
                );

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.TOS_PRIVACY_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CX_TYPE.toString())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void putTosPrivacy() {
        Mockito.when(tosPrivacyService.acceptOrDeclineTosPrivacy(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.empty());

        TosPrivacyBody request = TosPrivacyBody.builder()
                .tos(new TosPrivacyActionBody().action(TosPrivacyActionBody.ActionEnum.ACCEPT).version("1"))
                .build();

        webTestClient.put()
                .uri(PnBffRestConstants.TOS_PRIVACY_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), TosPrivacyBody.class)
                .headers(httpHeaders -> {
                    httpHeaders.set("x-pagopa-pn-uid", UID);
                    httpHeaders.set("x-pagopa-pn-cx-type", CX_TYPE.toString());
                })
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Void.class);

        Mockito.verify(tosPrivacyService).acceptOrDeclineTosPrivacy(Mockito.anyString(), Mockito.any(), Mockito.any());
    }

    @Test
    void putTosPrivacyError() {
        Mockito.doThrow(new PnBffException("Err", "Err", "Err", 500, "Err", null))
                .when(tosPrivacyService).acceptOrDeclineTosPrivacy(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<Mono<TosPrivacyBody>>any()
                );

        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.TOS_PRIVACY_PATH).build())
                .header(PnBffRestConstants.UID_HEADER, UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CX_TYPE.toString())
                .body(Mono.just(new TosPrivacyBody()), TosPrivacyBody.class)
                .exchange()
                .expectStatus()
                .is5xxServerError();

    }
}
