package it.pagopa.pn.bff.pnclient.externalregistries;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import it.pagopa.pn.bff.mocks.UserMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnInfoPaClientImpl.class})
@ExtendWith(SpringExtension.class)
class PnInfoPaClientImplTest {
    private final UserMock userMock = new UserMock();
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
    @Autowired
    private PnInfoPaClientImpl pnInfoPaClient;
    @MockBean
    private InfoPaApi infoPaApi;

    @Test
    void getInstitutionsTest() {
        when(infoPaApi.getInstitutions(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.fromIterable(institutionAndProductMock.getInstitutionResourcePNMock()));

        StepVerifier.create(pnInfoPaClient.getInstitutions(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectNextSequence(institutionAndProductMock.getInstitutionResourcePNMock()).verifyComplete();
    }

    @Test
    void getInstitutionsTestError() {
        when(infoPaApi.getInstitutions(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnInfoPaClient.getInstitutions(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectError(PnBffException.class).verify();
    }

    @Test
    void getInstitutionProductsTest() {
        when(infoPaApi.getInstitutionProducts(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.fromIterable(institutionAndProductMock.getProductResourcePNMock()));

        StepVerifier.create(pnInfoPaClient.getInstitutionProduct(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectNextSequence(institutionAndProductMock.getProductResourcePNMock()).verifyComplete();
    }

    @Test
    void getInstitutionProductsTestError() {
        when(infoPaApi.getInstitutionProducts(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnInfoPaClient.getInstitutionProduct(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectError(PnBffException.class).verify();
    }

    @Test
    void getGroups() throws RestClientException {
        when(infoPaApi.getGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(PaGroupStatus.class)
        )).thenReturn(Flux.fromIterable(userMock.getPaGroupsMock()));

        StepVerifier.create(pnInfoPaClient.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                PaGroupStatus.ACTIVE
        )).expectNextSequence(userMock.getPaGroupsMock()).verifyComplete();
    }

    @Test
    void getGroupsError() {
        when(infoPaApi.getGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(PaGroupStatus.class)
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnInfoPaClient.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                PaGroupStatus.ACTIVE
        )).expectError(PnBffException.class).verify();
    }
}