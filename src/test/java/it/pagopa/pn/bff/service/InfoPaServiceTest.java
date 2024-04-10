package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.InfoPaMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
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
    private PnExternalRegistriesClientImpl pnExternalRegistriesClient;

    @BeforeEach
    void setup() {
        pnExternalRegistriesClient = mock(PnExternalRegistriesClientImpl.class);
        this.infoPaService = new InfoPaService(pnExternalRegistriesClient);
    }

    @Test
    void getInstitutionsTest() {
        when(pnExternalRegistriesClient.getInstitutions(Mockito.<String>any(), Mockito.<CxTypeAuthFleet>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<List<String>>any(), Mockito.<String>any()))
                .thenReturn(Flux.just(mock(InstitutionResourcePN.class)));
        when(infoPaMapper.toBffInstitution(any(InstitutionResourcePN.class)))
                .thenReturn(new BffInstitution());

        StepVerifier
                .create(infoPaService.getInstitutions("xPagopaPnUid", CxTypeAuthFleet.PA, "xPagopaPnCxId", "xPagopaPnSrcCh", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getInstitutionsTestError() {
        when(pnExternalRegistriesClient.getInstitutions(Mockito.<String>any(), Mockito.<CxTypeAuthFleet>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<List<String>>any(), Mockito.<String>any()))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(infoPaService.getInstitutions("xPagopaPnUid", CxTypeAuthFleet.PA, "xPagopaPnCxId", "xPagopaPnSrcCh", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getInstitutionProductTest() {
        when(pnExternalRegistriesClient.getInstitutionProduct(Mockito.<String>any(), Mockito.<CxTypeAuthFleet>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<List<String>>any(), Mockito.<String>any()))
                .thenReturn(Flux.just(mock(ProductResourcePN.class)));

        StepVerifier
                .create(infoPaService.getInstitutionProducts("xPagopaPnUid", CxTypeAuthFleet.PA, "xPagopaPnCxId", "xPagopaPnSrcCh", "xPagopaPnProductId", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getInstitutionProductTestError() {
        when(pnExternalRegistriesClient.getInstitutionProduct(Mockito.<String>any(), Mockito.<CxTypeAuthFleet>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(), Mockito.<List<String>>any(), Mockito.<String>any()))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(infoPaService.getInstitutionProducts("xPagopaPnUid", CxTypeAuthFleet.PA, "xPagopaPnCxId", "xPagopaPnSrcCh", "xPagopaPnProductId", List.of("xPagopaPnCxGroups"), "xPagopaPnSrcChDetails"))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}
