package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.mappers.InfoPaMapper;
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

@ContextConfiguration(classes = {InfoPaService.class})
public class InfoPaServiceTest {
    private final InfoPaMapper infoPaMapper = mock(InfoPaMapper.class);
    @Autowired
    private InfoPaService infoPaService;
    private PnInfoPaClientImpl pnInfoPaClient;

    @BeforeEach
    void setup() {
        pnInfoPaClient = mock(PnInfoPaClientImpl.class);
        this.infoPaService = new InfoPaService(pnInfoPaClient);
    }

    @Test
    void getInstitutionsTest() {
        when(pnInfoPaClient.getInstitutions(Mockito.<String>any(), Mockito.any(CxTypeAuthFleet.class), Mockito.<String>any(), Mockito.<String>any(), Mockito.<List<String>>any(), Mockito.<String>any()))
                .thenReturn(Flux.just(mock(InstitutionResourcePN.class)));
        when(infoPaMapper.toBffInstitution(any(InstitutionResourcePN.class)))
                .thenReturn(new BffInstitution());

        StepVerifier
                .create(infoPaService.getInstitutions("xPagopaPnUid", it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA, "xPagopaPnCxId", "xPagopaPnSrcCh", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getInstitutionsTestError() {
        when(pnInfoPaClient.getInstitutions(Mockito.<String>any(), Mockito.<CxTypeAuthFleet>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<List<String>>any(), Mockito.<String>any()))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(infoPaService.getInstitutions("xPagopaPnUid",it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA , "xPagopaPnCxId", "xPagopaPnSrcCh", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getInstitutionProductTest() {
        when(pnInfoPaClient.getInstitutionProduct(Mockito.<String>any(), Mockito.<CxTypeAuthFleet>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<List<String>>any(), Mockito.<String>any()))
                .thenReturn(Flux.just(mock(ProductResourcePN.class)));

        StepVerifier
                .create(infoPaService.getInstitutionProducts("xPagopaPnUid",it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA , "xPagopaPnCxId", "xPagopaPnSrcCh", "xPagopaPnProductId", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getInstitutionProductTestError() {
        when(pnInfoPaClient.getInstitutionProduct(Mockito.<String>any(), Mockito.<CxTypeAuthFleet>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<List<String>>any(), Mockito.<String>any()))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(infoPaService.getInstitutionProducts("xPagopaPnUid",it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA , "xPagopaPnCxId", "xPagopaPnSrcCh", "xPagopaPnProductId", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}
