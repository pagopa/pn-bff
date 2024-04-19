package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.mappers.institutionandproduct.InstitutionMapper;
import it.pagopa.pn.bff.mappers.institutionandproduct.ProductMapper;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.externalregistries.PnInfoPaClientImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PnBffConfigs.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class InstitutionAndProductPaServiceTest {

    private static PnInfoPaClientImpl pnInfoPaClient;
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
    @Autowired
    private PnBffConfigs pnBffConfigs;
    private InstitutionAndProductPaService institutionAndProductPaService;

    @BeforeAll
    public void setup() {
        pnInfoPaClient = mock(PnInfoPaClientImpl.class);
        institutionAndProductPaService = new InstitutionAndProductPaService(pnInfoPaClient, pnBffConfigs);
    }

    @Test
    void getInstitutionsTest() {
        List<BffInstitution> bffInstitutions = institutionAndProductMock.getInstitutionResourcePNSMock()
                .stream()
                .map(institution -> InstitutionMapper.modelMapper.toBffInstitution(institution, pnBffConfigs))
                .toList();

        when(pnInfoPaClient.getInstitutions(Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Flux.fromIterable(institutionAndProductMock.getInstitutionResourcePNSMock()));

        Flux<BffInstitution> result = institutionAndProductPaService.getInstitutions(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.SEARCH_CHANNEL,
                UserMock.PN_CX_GROUPS,
                UserMock.SEARCH_DETAILS);

        StepVerifier
                .create(result.collectList())
                .expectNext(bffInstitutions)
                .verifyComplete();
    }

    @Test
    void getInstitutionsTestError() {
        when(pnInfoPaClient.getInstitutions(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(institutionAndProductPaService.getInstitutions(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                        UserMock.PN_CX_ID,
                        UserMock.SEARCH_CHANNEL,
                        UserMock.PN_CX_GROUPS,
                        UserMock.SEARCH_DETAILS))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getInstitutionProductTest() {
        List<BffInstitutionProduct> bffInstitutionProducts = institutionAndProductMock.getProductResourcePNSMock()
                .stream()
                .map(product -> ProductMapper.modelMapper.toBffInstitutionProduct(product, pnBffConfigs, UserMock.INSTITUTION_ID))
                .toList();
        when(pnInfoPaClient.getInstitutionProduct(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Flux.fromIterable(institutionAndProductMock.getProductResourcePNSMock()));

        Flux<BffInstitutionProduct> result = institutionAndProductPaService.getInstitutionProducts(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.SEARCH_CHANNEL,
                UserMock.INSTITUTION_ID,
                UserMock.PN_CX_GROUPS,
                UserMock.SEARCH_DETAILS);

        StepVerifier
                .create(result.collectList())
                .expectNext(bffInstitutionProducts)
                .verifyComplete();
    }

    @Test
    void getInstitutionProductTestError() {
        when(pnInfoPaClient.getInstitutionProduct(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(institutionAndProductPaService.getInstitutionProducts(UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                        UserMock.PN_CX_ID,
                        UserMock.SEARCH_CHANNEL,
                        UserMock.INSTITUTION_ID,
                        UserMock.PN_CX_GROUPS,
                        UserMock.SEARCH_DETAILS))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}