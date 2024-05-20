package it.pagopa.pn.bff.pnclient.externalregistries;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.api.PaymentInfoApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoV21;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPgApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroupStatus;
import it.pagopa.pn.bff.mocks.PaInfoMock;
import it.pagopa.pn.bff.mocks.PaymentsMock;
import it.pagopa.pn.bff.mocks.RecipientInfoMock;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnExternalRegistriesClientImpl.class})
@ExtendWith(SpringExtension.class)
class PnExternalRegistriesClientImplTest {
    private final PaInfoMock paInfoMock = new PaInfoMock();
    private final RecipientInfoMock recipientInfoMock = new RecipientInfoMock();
    private final PaymentsMock paymentsMock = new PaymentsMock();
    @Autowired
    private PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    @MockBean
    private InfoPaApi infoPaApi;
    @MockBean
    private InfoPgApi infoPgApi;
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
        )).thenReturn(Flux.fromIterable(paInfoMock.getInstitutionResourcePNMock()));

        StepVerifier.create(pnExternalRegistriesClient.getInstitutions(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectNextSequence(paInfoMock.getInstitutionResourcePNMock()).verifyComplete();
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
        )).thenReturn(Flux.fromIterable(paInfoMock.getProductResourcePNMock()));

        StepVerifier.create(pnExternalRegistriesClient.getInstitutionProducts(
                UserMock.PN_UID,
                CxTypeAuthFleet.PA,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS
        )).expectNextSequence(paInfoMock.getProductResourcePNMock()).verifyComplete();
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
    void getGroupsPa() {
        when(infoPaApi.getGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(PaGroupStatus.class)
        )).thenReturn(Flux.fromIterable(paInfoMock.getPaGroupsMock()));

        StepVerifier.create(pnExternalRegistriesClient.getGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                PaGroupStatus.ACTIVE
        )).expectNextSequence(paInfoMock.getPaGroupsMock()).verifyComplete();
    }

    @Test
    void getGroupsPaError() {
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
    void getGroupsPg(){
        when(infoPgApi.getPgGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(PgGroupStatus.class)
        )).thenReturn(Flux.fromIterable(recipientInfoMock.getPgGroupsMock()));

        StepVerifier.create(pnExternalRegistriesClient.getPgGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                PgGroupStatus.ACTIVE
        )).expectNextSequence(recipientInfoMock.getPgGroupsMock()).verifyComplete();
    }

    @Test
    void getGroupsPgError(){
        when(infoPgApi.getPgGroups(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.any(PgGroupStatus.class)
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnExternalRegistriesClient.getPgGroups(
                UserMock.PN_UID,
                UserMock.PN_CX_ID,
                UserMock.PN_CX_GROUPS,
                PgGroupStatus.ACTIVE
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getPaymentsInfo() {
        List<PaymentInfoV21> paymentsInfoResponse = paymentsMock.getPaymentsInfoResponseMock();

        when(paymentInfoApi.getPaymentInfoV21(
                Mockito.anyList()
        )).thenReturn(Flux.fromIterable(paymentsInfoResponse));

        StepVerifier.create(pnExternalRegistriesClient.getPaymentsInfo(
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                paymentsMock.getPaymentsInfoRequestMock()
        )).expectNextSequence(paymentsInfoResponse).verifyComplete();
    }

    @Test
    void getPaymentsInfoError() {
        when(paymentInfoApi.getPaymentInfoV21(
                Mockito.anyList()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnExternalRegistriesClient.getPaymentsInfo(
                CxTypeAuthFleet.PF,
                UserMock.PN_CX_ID,
                paymentsMock.getPaymentsInfoRequestMock()
        )).expectError(WebClientResponseException.class).verify();
    }

    @Test
    void paymentsCart() {
        PaymentRequest paymentRequest = paymentsMock.getPaymentRequestMock();
        PaymentResponse paymentResponse = paymentsMock.getPaymentResponseMock();

        when(paymentInfoApi.checkoutCart(
                Mockito.any(PaymentRequest.class)
        )).thenReturn(Mono.just(paymentResponse));

        StepVerifier.create(pnExternalRegistriesClient.paymentsCart(
                paymentRequest
        )).expectNext(paymentResponse).verifyComplete();
    }

    @Test
    void paymentsCartError() {
        when(paymentInfoApi.checkoutCart(
                Mockito.any(PaymentRequest.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnExternalRegistriesClient.paymentsCart(
                paymentsMock.getPaymentRequestMock()
        )).expectError(WebClientResponseException.class).verify();
    }
}