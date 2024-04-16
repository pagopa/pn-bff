package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.PnBffConfigs;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import it.pagopa.pn.bff.pnclient.externalregistries.PnInfoPaClientImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {InstitutionAndProductPaService.class})
public class InstitutionAndProductPaServiceTest {
    @Autowired
    private static InstitutionAndProductPaService institutionAndProductPaService;
    private static PnInfoPaClientImpl pnInfoPaClient;
    private static PnBffConfigs pnBffConfigs;
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();

    @BeforeAll
    public static void setup() {
        pnInfoPaClient = mock(PnInfoPaClientImpl.class);
        pnBffConfigs = mock(PnBffConfigs.class);
        institutionAndProductPaService = new InstitutionAndProductPaService(pnInfoPaClient, pnBffConfigs);
    }

    @Test
    void getInstitutionsTest() {
        when(pnInfoPaClient.getInstitutions(Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Flux.fromIterable(institutionAndProductMock.getInstitutionResourcePNSMock()));
        when(pnBffConfigs.getSelfcareBaseUrl()).thenReturn("https://fooselfcare.com");
        when(pnBffConfigs.getSelfcareSendProdId()).thenReturn("foo-prod-id");

        Flux<BffInstitution> result = institutionAndProductPaService.getInstitutions(
                "xPagopaPnUid", it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                "xPagopaPnCxId",
                "xPagopaPnSrcCh",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnSrcChDetails");

        StepVerifier
                .create(result.collectList())
                .expectNext(institutionAndProductMock.getBffInstitutionsMock())
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
                .thenReturn(Flux.fromIterable(institutionAndProductMock.getProductResourcePNSMock()));
        when(pnBffConfigs.getSelfcareBaseUrl()).thenReturn("https://fooselfcare.com");

        Flux<BffInstitutionProduct> result = institutionAndProductPaService.getInstitutionProducts(
                "xPagopaPnUid", it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                "xPagopaPnCxId",
                "xPagopaPnSrcCh",
                "fooInstitutionId",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnSrcChDetails");

        StepVerifier
                .create(result.collectList())
                .expectNext(institutionAndProductMock.getBffInstitutionProductsMock())
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
