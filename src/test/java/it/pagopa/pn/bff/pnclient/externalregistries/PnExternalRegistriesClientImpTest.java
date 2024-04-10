package it.pagopa.pn.bff.pnclient.externalregistries;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.InstitutionResourcePN;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnExternalRegistriesClientImpl.class})
@ExtendWith(SpringExtension.class)
public class PnExternalRegistriesClientImpTest {
    @Autowired
    private PnExternalRegistriesClientImpl pnExternalRegistriesClientImpl;
    @MockBean
    private InfoPaApi infoPaApi;

    @Test
    void getInstitutionsTest() {
        when(infoPaApi.getInstitutions(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<List<String>>any(),
                Mockito.<String>any()
        )).thenReturn(Flux.just(mock(InstitutionResourcePN.class)));

        StepVerifier.create(pnExternalRegistriesClientImpl.getInstitutions(
                "xPagopaPnUid",
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                "xPagopaPnCxId",
                "xPagopaPnSrcCh",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnSrcChDetails"
        )).expectNextCount(1).verifyComplete();
    }

    @Test
    void getInstitutionsTestError() {
        when(infoPaApi.getInstitutions(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<List<String>>any(),
                Mockito.<String>any()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnExternalRegistriesClientImpl.getInstitutions(
                "xPagopaPnUid",
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                "xPagopaPnCxId",
                "xPagopaPnSrcCh",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnSrcChDetails"
        )).expectError(PnBffException.class).verify();
    }

    @Test
    void getInstitutionProductsTest() {
        when(infoPaApi.getInstitutionProducts(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<List<String>>any(),
                Mockito.<String>any()
        )).thenReturn(Flux.just(mock(it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.ProductResourcePN.class)));

        StepVerifier.create(pnExternalRegistriesClientImpl.getInstitutionProduct(
                "xPagopaPnUid",
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                "xPagopaPnSrcCh",
                "xPagopaPnSrcChDetails",
                "xPagopaPnProductType",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnCxId"
        )).expectNextCount(1).verifyComplete();
    }

    @Test
    void getInstitutionProductsTestError() {
        when(infoPaApi.getInstitutionProducts(
                Mockito.<String>any(),
                Mockito.<CxTypeAuthFleet>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<List<String>>any(),
                Mockito.<String>any()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnExternalRegistriesClientImpl.getInstitutionProduct(
                "xPagopaPnUid",
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet.PA,
                "xPagopaPnSrcCh",
                "xPagopaPnSrcChDetails",
                "xPagopaPnProductType",
                List.of("xPagopaPnCxGroups"),
                "xPagopaPnCxId"
        )).expectError(PnBffException.class).verify();
    }
}