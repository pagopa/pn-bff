package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.institutionandproduct.InstitutionMapper;
import it.pagopa.pn.bff.mappers.institutionandproduct.ProductMapper;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.InstitutionAndProductPaService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@WebFluxTest(InstitutionAndProductPaController.class)
@TestPropertySource(locations="classpath:application-test.properties")
public class InstitutionAndProductPaControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private InstitutionAndProductPaService institutionAndProductPaService;

    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();

    @Test
    void getInstitutionsV1() {
        List<BffInstitution> bffInstitutions = institutionAndProductMock.getInstitutionResourcePNSMock()
                .stream()
                .map(InstitutionMapper.INSTITUTION_MAPPER::toBffInstitution)
                .toList();
        Mockito
                .when(institutionAndProductPaService.getInstitutions(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.nullable(List.class),
                        Mockito.nullable(String.class)))
                .thenReturn(Flux.fromIterable(bffInstitutions));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTITUTIONS_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.SOURCECHANNEL_HEADER, UserMock.SOURCECHANNEL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BffInstitution.class)
                .isEqualTo(institutionAndProductMock.getBffInstitutionsMock());
    }

    @Test
    void getInstitutionsV1Error() {
        Mockito
                .doThrow(new PnBffException("Err", "Err", "Err", 404, "Err", null))
                .when(institutionAndProductPaService).getInstitutions(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.nullable(List.class),
                        Mockito.nullable(String.class));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTITUTIONS_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.SOURCECHANNEL_HEADER, UserMock.SOURCECHANNEL)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getInstitutionProductV1() {
        List<BffInstitutionProduct> bffInstitutionProducts = institutionAndProductMock.getProductResourcePNSMock()
                .stream()
                .map(ProductMapper.PRODUCT_MAPPER::toBffInstitutionProduct)
                .toList();
        Mockito
                .when(institutionAndProductPaService.getInstitutionProducts(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.nullable(List.class),
                        Mockito.nullable(String.class)))
                .thenReturn(Flux.fromIterable(bffInstitutionProducts));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTITUTIONS_PATH + "/{institutionId}/products").build("institutionId"))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.SOURCECHANNEL_HEADER, UserMock.SOURCECHANNEL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BffInstitutionProduct.class)
                .isEqualTo(institutionAndProductMock.getBffInstitutionProductsMock());
    }

    @Test
    void getInstitutionProductErrorV1Error() {
        Mockito
                .doThrow(new PnBffException("Err", "Err", "Err", 404, "Err", null))
                .when(institutionAndProductPaService).getInstitutionProducts(
                        Mockito.anyString(),
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.nullable(List.class),
                        Mockito.nullable(String.class));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.INSTITUTIONS_PATH + "/{institutionId}/products").build("institutionId"))
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PA.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .header(PnBffRestConstants.SOURCECHANNEL_HEADER, UserMock.SOURCECHANNEL)
                .exchange()
                .expectStatus().isNotFound();
    }

}
