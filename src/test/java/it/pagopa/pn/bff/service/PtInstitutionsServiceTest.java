package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_pt.model.PtDelegationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtDelegationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtInstitutionsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.ptinstitutions.PtInstitutionsMapper;
import it.pagopa.pn.bff.mocks.PtInstitutionsMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class PtInstitutionsServiceTest {

    private final PtInstitutionsMock ptInstitutionsMock = new PtInstitutionsMock();
    private PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private PtInstitutionsService ptInstitutionsService;

    @BeforeEach
    void setup() {
        pnExternalRegistriesClient = mock(PnExternalRegistriesClientImpl.class);
        ptInstitutionsService = new PtInstitutionsService(pnExternalRegistriesClient, new PnBffExceptionUtility(new ObjectMapper()));
    }

    @Test
    void getPtInstitutions() {
        BffPtInstitutionsResponse expected = PtInstitutionsMapper.modelMapper
                .toBffPtInstitutionsResponse(ptInstitutionsMock.getPtInstitutionsResponseMock());

        when(pnExternalRegistriesClient.getPtInstitutions(
                Mockito.anyString(), Mockito.any(), Mockito.anyString(),
                Mockito.anyString(), Mockito.any(), Mockito.anyInt(), Mockito.nullable(String.class)))
                .thenReturn(Mono.just(ptInstitutionsMock.getPtInstitutionsResponseMock()));

        StepVerifier
                .create(ptInstitutionsService.getPtInstitutions(
                        UserMock.PN_UID, CxTypeAuthFleet.PT, UserMock.PN_CX_ID,
                        "Ente", BffPtDelegationStatus.ACTIVE, 10, null))
                .expectNext(expected)
                .verifyComplete();

        verify(pnExternalRegistriesClient).getPtInstitutions(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.msclient.external_registries_pt.model.CxTypeAuthFleet.PT,
                UserMock.PN_CX_ID,
                "Ente",
                PtDelegationStatus.ACTIVE,
                10,
                null);
    }

    @Test
    void getPtInstitutionsError() {
        when(pnExternalRegistriesClient.getPtInstitutions(
                Mockito.anyString(), Mockito.any(), Mockito.anyString(),
                Mockito.nullable(String.class), Mockito.any(), Mockito.nullable(Integer.class), Mockito.nullable(String.class)))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(ptInstitutionsService.getPtInstitutions(
                        UserMock.PN_UID, CxTypeAuthFleet.PT, UserMock.PN_CX_ID,
                        null, null, null, null))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}