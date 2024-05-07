package it.pagopa.pn.bff.pnclient.externalregistries;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.api.PaymentInfoApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoV21;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import it.pagopa.pn.bff.mocks.PaymentsMock;
import it.pagopa.pn.bff.mocks.UserMock;
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

@ContextConfiguration(classes = {PnExternalRegistriesClientImpl.class})
@ExtendWith(SpringExtension.class)
class PnExternalRegistriesClientImplTest {
    private final UserMock userMock = new UserMock();
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
    private final PaymentsMock paymentsMock = new PaymentsMock();
    @Autowired
    private PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    @MockBean
    private InfoPaApi infoPaApi;
    @MockBean
    private PaymentInfoApi paymentInfoApi;

    @Test
    void getInstitutions() {
        when(infoPaApi.getInstitutions(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(String.class)
        )).thenReturn(Flux.fromIterable(institutionAndProductMock.getInstitutionResourcePNMock()));

        StepVerifier.create(pnExternalRegistriesClient.getInstitutions(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectNextSequence(institutionAndProductMock.getInstitutionResourcePNMock()).verifyComplete();
    }

    @Test
    void getInstitutionsError() {
        when(infoPaApi.getInstitutions(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(String.class)
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnExternalRegistriesClient.getInstitutions(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getInstitutionProducts() {
        when(infoPaApi.getInstitutionProducts(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(String.class)
        )).thenReturn(Flux.fromIterable(institutionAndProductMock.getProductResourcePNMock()));

        StepVerifier.create(pnExternalRegistriesClient.getInstitutionProducts(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectNextSequence(institutionAndProductMock.getProductResourcePNMock()).verifyComplete();
    }

    @Test
    void getInstitutionProductsError() {
        when(infoPaApi.getInstitutionProducts(
                Mockito.anyString(),
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.nullable(String.class)
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnExternalRegistriesClient.getInstitutionProducts(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getGroups() {
        when(infoPaApi.getGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(PaGroupStatus.class)
        )).thenReturn(Flux.fromIterable(userMock.getPaGroupsMock()));

        StepVerifier.create(pnExternalRegistriesClient.getGroups(
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

        StepVerifier.create(pnExternalRegistriesClient.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                PaGroupStatus.ACTIVE
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getPaymentsInfo() {
        List<PaymentInfoV21> paymentsInfoResponse = paymentsMock.getPaymentsInfoResponseMock();

        when(paymentInfoApi.getPaymentInfoV21(
                Mockito.anyList()
        )).thenReturn(Flux.fromIterable(paymentsInfoResponse));

        StepVerifier.create(pnExternalRegistriesClient.getPaymentsInfo(
                paymentsMock.getPaymentsInfoRequestMock()
        )).expectNextSequence(paymentsInfoResponse).verifyComplete();
    }

    @Test
    void getPaymentsInfoError() {
        when(paymentInfoApi.getPaymentInfoV21(
                Mockito.anyList()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnExternalRegistriesClient.getPaymentsInfo(
                paymentsMock.getPaymentsInfoRequestMock()
        )).expectError(WebClientResponseException.class).verify();
    }
}