package it.pagopa.pn.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoV21;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentInfoItem;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentResponse;
import it.pagopa.pn.bff.mappers.payments.PaymentsCartMapper;
import it.pagopa.pn.bff.mappers.payments.PaymentsInfoMapper;
import it.pagopa.pn.bff.mocks.PaymentsMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentsServiceTest {
    private static PaymentsService paymentsService;
    private static PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private static PnBffExceptionUtility pnBffExceptionUtility;
    private final PaymentsMock paymentsMock = new PaymentsMock();

    @BeforeAll
    public static void setup() {
        pnExternalRegistriesClient = mock(PnExternalRegistriesClientImpl.class);
        pnBffExceptionUtility = new PnBffExceptionUtility(new ObjectMapper());
        paymentsService = new PaymentsService(pnExternalRegistriesClient, pnBffExceptionUtility);
    }

    @Test
    void getPaymentsInfo() {
        List<PaymentInfoV21> response = paymentsMock.getPaymentsInfoResponseMock();

        when(pnExternalRegistriesClient.getPaymentsInfo(
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Flux.fromIterable(response));

        Mono<List<BffPaymentInfoItem>> result = paymentsService.getPaymentsInfo(
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                Flux.fromIterable(paymentsMock.getBffPaymentsInfoRequestMock())
        );

        StepVerifier.create(result)
                .expectNext(response.stream()
                        .map(PaymentsInfoMapper.modelMapper::mapPaymentInfoResponse).toList()
                )
                .verifyComplete();
    }

    @Test
    void getPaymentsInfoError() {
        when(pnExternalRegistriesClient.getPaymentsInfo(
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(Flux.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<List<BffPaymentInfoItem>> result = paymentsService.getPaymentsInfo(
                it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA.PF,
                UserMock.PN_CX_ID,
                Flux.fromIterable(paymentsMock.getBffPaymentsInfoRequestMock())
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }

    @Test
    void paymentsCart() {
        when(pnExternalRegistriesClient.paymentsCart(
                Mockito.any(PaymentRequest.class)
        )).thenReturn(Mono.just(paymentsMock.getPaymentResponseMock()));

        Mono<BffPaymentResponse> result = paymentsService.paymentsCart(
                Mono.just(paymentsMock.getBffPaymentRequestMock())
        );

        StepVerifier.create(result)
                .expectNext(PaymentsCartMapper.modelMapper.mapPaymentResponse(paymentsMock.getPaymentResponseMock()))
                .verifyComplete();
    }

    @Test
    void paymentsCartError() {
        when(pnExternalRegistriesClient.paymentsCart(
                Mockito.any(PaymentRequest.class)
        )).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<BffPaymentResponse> result = paymentsService.paymentsCart(
                Mono.just(paymentsMock.getBffPaymentRequestMock())
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 404)
                .verify();
    }
}