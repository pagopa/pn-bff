package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.InfoPaService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@WebFluxTest(InfoPaController.class)
public class InfoPaControllerTest {
    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private InfoPaService infoPaService;

    @Test
    void getInstitutionsV1() {
        Mockito
                .when(infoPaService.getInstitutions(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<List<String>>any(),
                        Mockito.<String>any()))
                .thenReturn(Flux.just(new BffInstitution()));
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTIUTIONS_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, "iud")
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, "institutionId")
                .header(PnBffRestConstants.SOURCECHANNEL_HEADER, "WEB")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BffInstitution.class);
    }

    @Test
    void getInstitutionsV1Error() {
        Mockito
                .doThrow(new PnBffException("Err", "Err", "Err", 404, "Err", null))
                .when(infoPaService).getInstitutions(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<List<String>>any(),
                        Mockito.<String>any());
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTIUTIONS_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, "iud")
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, "institutionId")
                .header(PnBffRestConstants.SOURCECHANNEL_HEADER, "WEB")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getInstitutionProductV1() {
        Mockito
                .when(infoPaService.getInstitutionProducts(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<List<String>>any(),
                        Mockito.<String>any()))
                .thenReturn(Flux.just(new BffInstitutionProduct()));
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTIUTIONS_PATH + "/{institutionId}/products").build("institutionId"))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, "iud")
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, "institutionId")
                .header(PnBffRestConstants.SOURCECHANNEL_HEADER, "WEB")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BffInstitution.class);
    }

    @Test
    void getInstitutionProductErrorV1Error() {
        Mockito
                .doThrow(new PnBffException("Err", "Err", "Err", 404, "Err", null))
                .when(infoPaService).getInstitutionProducts(
                        Mockito.<String>any(),
                        Mockito.<CxTypeAuthFleet>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<String>any(),
                        Mockito.<List<String>>any(),
                        Mockito.<String>any());
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTIUTIONS_PATH + "/{institutionId}/products").build("institutionId"))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, "iud")
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, "institutionId")
                .header(PnBffRestConstants.SOURCECHANNEL_HEADER, "WEB")
                .exchange()
                .expectStatus().isNotFound();
    }

}
