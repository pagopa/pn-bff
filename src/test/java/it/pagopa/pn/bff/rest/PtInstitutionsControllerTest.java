package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtDelegationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtInstitutionsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.ptinstitutions.PtInstitutionsMapper;
import it.pagopa.pn.bff.mocks.PtInstitutionsMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.PtInstitutionsService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(PtInstitutionsController.class)
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PnBffConfigs.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class PtInstitutionsControllerTest {

    private final PtInstitutionsMock ptInstitutionsMock = new PtInstitutionsMock();
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private PtInstitutionsService ptInstitutionsService;

    @Test
    void getPtInstitutionsV1() {
        BffPtInstitutionsResponse expected = PtInstitutionsMapper.modelMapper
                .toBffPtInstitutionsResponse(ptInstitutionsMock.getPtInstitutionsResponseMock());

        Mockito.when(ptInstitutionsService.getPtInstitutions(
                        Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.anyString(),
                        Mockito.nullable(String.class), Mockito.nullable(BffPtDelegationStatus.class),
                        Mockito.nullable(Integer.class), Mockito.nullable(String.class)))
                .thenReturn(Mono.just(expected));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.PT_INSTITUTIONS_PATH)
                        .queryParam("status", BffPtDelegationStatus.ACTIVE.getValue())
                        .queryParam("limit", 10)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PT.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BffPtInstitutionsResponse.class)
                .isEqualTo(expected);

        Mockito.verify(ptInstitutionsService).getPtInstitutions(
                UserMock.PN_UID, CxTypeAuthFleet.PT, UserMock.PN_CX_ID,
                null, BffPtDelegationStatus.ACTIVE, 10, null);
    }

    @Test
    void getPtInstitutionsV1Error() {
        Mockito.when(ptInstitutionsService.getPtInstitutions(
                        Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.anyString(),
                        Mockito.nullable(String.class), Mockito.nullable(BffPtDelegationStatus.class),
                        Mockito.nullable(Integer.class), Mockito.nullable(String.class)))
                .thenReturn(Mono.error(new PnBffException("Error Message", "Error Description", HttpStatus.NOT_FOUND.value(), "Error Code")));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(PnBffRestConstants.PT_INSTITUTIONS_PATH).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PnBffRestConstants.UID_HEADER, UserMock.PN_UID)
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PT.getValue())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .exchange()
                .expectStatus().isNotFound();
    }
}