package it.pagopa.pn.bff.config;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.rest.TosPrivacyController;
import it.pagopa.pn.bff.service.TosPrivacyService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@WebFluxTest(TosPrivacyController.class)
@Import(TrailingSlashConfig.class)
class TrailingSlashConfigTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    private TosPrivacyService tosPrivacyService;

    @ParameterizedTest
    @ValueSource(strings = {"", "/"})
    void pathMatchesWithAndWithoutTrailingSlash(String suffix) {
        Mockito.when(tosPrivacyService.getTosPrivacy(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PnBffRestConstants.TOS_PRIVACY_PATH + suffix)
                        .queryParam("type", "TOS")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.toString())
                .exchange()
                .expectStatus()
                .isOk();
    }
}
