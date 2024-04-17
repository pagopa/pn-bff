package it.pagopa.pn.bff.pnclient.externalregistries;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnInfoPaClientImpl.class})
@ExtendWith(SpringExtension.class)
public class PnExternalRegistriesClientImplTest {
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
        )).thenReturn(Flux.fromIterable(institutionAndProductMock.getInstitutionResourcePNSMock()));

        StepVerifier.create(pnInfoPaClient.getInstitutions(
                "xPagopaPnUid",
                CxTypeAuthFleet.PA,
                "xPagopaPnCxId",
                "xPagopaPnSrcCh",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnSrcChDetails"
        )).expectNextSequence(institutionAndProductMock.getInstitutionResourcePNSMock()).verifyComplete();
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
                "xPagopaPnUid",
                CxTypeAuthFleet.PA,
                "xPagopaPnCxId",
                "xPagopaPnSrcCh",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnSrcChDetails"
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
        )).thenReturn(Flux.fromIterable(institutionAndProductMock.getProductResourcePNSMock()));

        StepVerifier.create(pnInfoPaClient.getInstitutionProduct(
                "xPagopaPnUid",
                CxTypeAuthFleet.PA,
                "xPagopaPnSrcCh",
                "xPagopaPnSrcChDetails",
                "xPagopaPnProductType",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnCxId"
        )).expectNextSequence(institutionAndProductMock.getProductResourcePNSMock()).verifyComplete();
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
                "xPagopaPnUid",
                CxTypeAuthFleet.PA,
                "xPagopaPnSrcCh",
                "xPagopaPnSrcChDetails",
                "xPagopaPnProductType",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnCxId"
        )).expectError(PnBffException.class).verify();
    }
}