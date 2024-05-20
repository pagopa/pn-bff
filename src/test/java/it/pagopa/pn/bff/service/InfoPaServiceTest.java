package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPaGroup;
import it.pagopa.pn.bff.mappers.infopa.GroupsMapper;
import it.pagopa.pn.bff.mappers.infopa.InstitutionMapper;
import it.pagopa.pn.bff.mappers.infopa.ProductMapper;
import it.pagopa.pn.bff.mocks.PaInfoMock;
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
class InfoPaServiceTest {

    private static PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    private final PaInfoMock paInfoMock = new PaInfoMock();
    private final UserMock userMock = new UserMock();

    @Autowired
    private PnBffConfigs pnBffConfigs;
    private InfoPaService infoPaService;

    @BeforeAll
    public void setup() {
        pnExternalRegistriesClient = mock(PnExternalRegistriesClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        infoPaService = new InfoPaService(pnExternalRegistriesClient, pnBffConfigs, pnBffExceptionUtility);
    }

    @Test
    void getInstitutions() {
        List<BffInstitution> bffInstitutions = paInfoMock.getInstitutionResourcePNMock()
                .stream()
                .map(institution -> InstitutionMapper.modelMapper.toBffInstitution(institution, pnBffConfigs))
                .toList();

        when(pnExternalRegistriesClient.getInstitutions(Mockito.anyString(), Mockito.any(CxTypeAuthFleet.class), Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Flux.fromIterable(paInfoMock.getInstitutionResourcePNMock()));

        Flux<BffInstitution> result = infoPaService.getInstitutions(
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
                .create(infoPaService.getInstitutions(
                        UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                        UserMock.PN_CX_ID,
                        UserMock.PN_CX_GROUPS))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getInstitutionProduct() {
        List<BffInstitutionProduct> bffInstitutionProducts = paInfoMock.getProductResourcePNMock()
                .stream()
                .map(product -> ProductMapper.modelMapper.toBffInstitutionProduct(product, pnBffConfigs, UserMock.PN_CX_ID))
                .toList();
        when(pnExternalRegistriesClient.getInstitutionProducts(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyList()))
                .thenReturn(Flux.fromIterable(paInfoMock.getProductResourcePNMock()));

        Flux<BffInstitutionProduct> result = infoPaService.getInstitutionProducts(
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
                .create(infoPaService.getInstitutionProducts(UserMock.PN_UID,
                        it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                        UserMock.PN_CX_ID,
                        UserMock.PN_CX_GROUPS))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void getGroups() {
        List<BffPaGroup> groups = paInfoMock.getPaGroupsMock()
                .stream()
                .map(GroupsMapper.modelMapper::mapGroups)
                .toList();

        when(pnExternalRegistriesClient.getPaGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(PaGroupStatus.class)
        )).thenReturn(Flux.fromIterable(paInfoMock.getPaGroupsMock()));

        Flux<BffPaGroup> result = infoPaService.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                null
        );

        StepVerifier.create(result.collectList())
                .expectNext(groups)
                .verifyComplete();
    }

    @Test
    void getGroupsError() {
        when(pnExternalRegistriesClient.getPaGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(PaGroupStatus.class)
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Flux<BffPaGroup> result = infoPaService.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                null
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

}