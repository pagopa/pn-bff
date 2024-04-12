package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.mappers.InstitutionProductMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnInfoPaClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {InstitutionAndProductPaService.class})
public class InstitutionAndProductPaServiceTest {
    private final InstitutionProductMapper institutionProductMapper = mock(InstitutionProductMapper.class);
    @Autowired
    private InstitutionAndProductPaService institutionAndProductPaService;
    private PnInfoPaClientImpl pnInfoPaClient;

    @BeforeEach
    void setup() {
        pnInfoPaClient = mock(PnInfoPaClientImpl.class);
        this.institutionAndProductPaService = new InstitutionAndProductPaService(pnInfoPaClient);
    }

    @Test
    void getInstitutionsTest() {
        when(pnInfoPaClient.getInstitutions(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Flux.just(mock(InstitutionResourcePN.class)));
        when(institutionProductMapper.toBffInstitution(any(InstitutionResourcePN.class)))
                .thenReturn(new BffInstitution());

        StepVerifier
                .create(institutionAndProductPaService.getInstitutions("xPagopaPnUid", it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA, "xPagopaPnCxId", "xPagopaPnSrcCh", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getInstitutionsTestError() {
        when(pnInfoPaClient.getInstitutions(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(institutionAndProductPaService.getInstitutions("xPagopaPnUid",it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA , "xPagopaPnCxId", "xPagopaPnSrcCh", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getInstitutionProductTest() {
        when(pnInfoPaClient.getInstitutionProduct(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Flux.just(mock(ProductResourcePN.class)));

        StepVerifier
                .create(institutionAndProductPaService.getInstitutionProducts("xPagopaPnUid",it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA , "xPagopaPnCxId", "xPagopaPnSrcCh", "xPagopaPnProductId", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getInstitutionProductTestError() {
        when(pnInfoPaClient.getInstitutionProduct(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(institutionAndProductPaService.getInstitutionProducts("xPagopaPnUid",it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA , "xPagopaPnCxId", "xPagopaPnSrcCh", "xPagopaPnProductId", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}
