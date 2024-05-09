package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.mappers.institutionandproduct.InstitutionMapper;
import it.pagopa.pn.bff.mappers.institutionandproduct.ProductMapper;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
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
class InstitutionAndProductPaServiceTest {

    private static PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
    @Autowired
    private PnBffConfigs pnBffConfigs;
    private InstitutionAndProductPaService institutionAndProductPaService;

    @BeforeAll
    public void setup() {
        pnExternalRegistriesClient = mock(PnExternalRegistriesClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        institutionAndProductPaService = new InstitutionAndProductPaService(pnExternalRegistriesClient, pnBffConfigs, pnBffExceptionUtility);
    }

    @Test
    void getInstitutions() {
        List<BffInstitution> bffInstitutions = institutionAndProductMock.getInstitutionResourcePNMock()
                .stream()
                .map(institution -> InstitutionMapper.modelMapper.toBffInstitution(institution, pnBffConfigs))
                .toList();

        when(pnExternalRegistriesClient.getInstitutions(Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Flux.fromIterable(institutionAndProductMock.getInstitutionResourcePNMock()));

        Flux<BffInstitution> result = institutionAndProductPaService.getInstitutions(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS);

        StepVerifier
                .create(result.collectList())
                .expectNext(bffInstitutions)
                .verifyComplete();
    }

    @Test
    void getInstitutionsError() {
        when(pnExternalRegistriesClient.getInstitutions(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(institutionAndProductPaService.getInstitutions(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                        UserMock.PN_CX_ID,
                        UserMock.PN_CX_GROUPS))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getInstitutionProduct() {
        List<BffInstitutionProduct> bffInstitutionProducts = institutionAndProductMock.getProductResourcePNMock()
                .stream()
                .map(product -> ProductMapper.modelMapper.toBffInstitutionProduct(product, pnBffConfigs, UserMock.PN_CX_ID))
                .toList();
        when(pnExternalRegistriesClient.getInstitutionProducts(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Flux.fromIterable(institutionAndProductMock.getProductResourcePNMock()));

        Flux<BffInstitutionProduct> result = institutionAndProductPaService.getInstitutionProducts(
                UserMock.PN_UID,
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS);

        StepVerifier
                .create(result.collectList())
                .expectNext(bffInstitutionProducts)
                .verifyComplete();
    }

    @Test
    void getInstitutionProductError() {
        when(pnExternalRegistriesClient.getInstitutionProducts(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier
                .create(institutionAndProductPaService.getInstitutionProducts(UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                        UserMock.PN_CX_ID,
                        UserMock.PN_CX_GROUPS))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}