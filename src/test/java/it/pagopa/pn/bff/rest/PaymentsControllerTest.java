package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.payments.PaymentsCartMapper;
import it.pagopa.pn.bff.mappers.payments.PaymentsInfoMapper;
import it.pagopa.pn.bff.mappers.payments.PaymentsTppMapper;
import it.pagopa.pn.bff.mocks.PaymentsMock;
import it.pagopa.pn.bff.mocks.UserMock;
import it.pagopa.pn.bff.service.PaymentsService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import it.pagopa.pn.bff.utils.helpers.FluxMatcher;
import it.pagopa.pn.bff.utils.helpers.MonoMatcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(PaymentsController.class)
class PaymentsControllerTest {
    private final PaymentsMock paymentsMock = new PaymentsMock();
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private PaymentsService paymentsService;

    @Test
    void getPaymentsInfo() {
        List<PaymentInfoRequest> request = paymentsMock.getBffPaymentsInfoRequestMock();
        List<BffPaymentInfoItem> response = paymentsMock.getPaymentsInfoResponseMock()
                .stream()
                .map(PaymentsInfoMapper.modelMapper::mapPaymentInfoResponse)
                .toList();

        when(paymentsService.getPaymentsInfo(
                Mockito.any(CxTypeAuthFleet.class),
                Mockito.anyString(),
                Mockito.any()
        )).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PAYMENTS_INFO_PATH)
                                .build())
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BffPaymentInfoItem.class)
                .isEqualTo(response);

        Mockito.verify(paymentsService).getPaymentsInfo(
                eq(CxTypeAuthFleet.PF),
                eq(UserMock.PN_CX_ID),
                argThat(new FluxMatcher<>(Flux.fromIterable(request)))
        );
    }

    @Test
    void getPaymentsInfoError() {
        List<PaymentInfoRequest> request = paymentsMock.getBffPaymentsInfoRequestMock();

        Mockito.when(paymentsService.getPaymentsInfo(
                        Mockito.any(CxTypeAuthFleet.class),
                        Mockito.anyString(),
                        Mockito.any()
                ))
                .thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PAYMENTS_INFO_PATH)
                                .build())
                .header(PnBffRestConstants.CX_TYPE_HEADER, CxTypeAuthFleet.PF.toString())
                .header(PnBffRestConstants.CX_ID_HEADER, UserMock.PN_CX_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(paymentsService).getPaymentsInfo(
                eq(CxTypeAuthFleet.PF),
                eq(UserMock.PN_CX_ID),
                argThat(new FluxMatcher<>(Flux.fromIterable(request)))
        );
    }

    @Test
    void paymentsCart() {
        BffPaymentRequest request = paymentsMock.getBffPaymentRequestMock();
        BffPaymentResponse response = PaymentsCartMapper.modelMapper.mapPaymentResponse(paymentsMock.getPaymentResponseMock());

        when(paymentsService.paymentsCart(
                Mockito.any()
        )).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PAYMENTS_CART_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffPaymentResponse.class)
                .isEqualTo(response);

        Mockito.verify(paymentsService).paymentsCart(
                argThat(new MonoMatcher<>(Mono.just(request)))
        );
    }

    @Test
    void paymentsCartError() {
        BffPaymentRequest request = paymentsMock.getBffPaymentRequestMock();

        when(paymentsService.paymentsCart(
                Mockito.any()
        )).thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));


        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PAYMENTS_CART_PATH)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(paymentsService).paymentsCart(
                argThat(new MonoMatcher<>(Mono.just(request)))
        );
    }

    @Test
    void paymentsTpp() {
        BffPaymentTppResponse response = PaymentsTppMapper.modelMapper.mapPaymentTppResponse(paymentsMock.getPaymentUrlResponse());

        when(paymentsService.paymentsTpp(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PAYMENTS_TPP_PATH)
                                .queryParam("retrievalId", "0e4c6629-8753-234s-b0da-1f796999ec2-15038637960920")
                                .queryParam("noticeCode", "333333333333333333")
                                .queryParam("paTaxId", "77777777777")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BffPaymentTppResponse.class)
                .isEqualTo(response);

        Mockito.verify(paymentsService).paymentsTpp(
                eq("0e4c6629-8753-234s-b0da-1f796999ec2-15038637960920"),
                eq("333333333333333333"),
                eq("77777777777")
        );
    }

    @Test
    void paymentsTppError() {
        when(paymentsService.paymentsTpp(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(Mono.error(new PnBffException("Not Found", "Not Found", 404, "NOT_FOUND")));

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(PnBffRestConstants.PAYMENTS_TPP_PATH)
                                .queryParam("retrievalId", "0e4c6629-8753-234s-b0da-1f796999ec2-15038637960920")
                                .queryParam("noticeCode", "333333333333333333")
                                .queryParam("paTaxId", "77777777777")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

        Mockito.verify(paymentsService).paymentsTpp(
                eq("0e4c6629-8753-234s-b0da-1f796999ec2-15038637960920"),
                eq("333333333333333333"),
                eq("77777777777")
        );
    }
}