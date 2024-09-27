package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.PaymentsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.service.PaymentsService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class PaymentsController implements PaymentsApi {
    private final PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    /**
     * POST bff/v1/payments/info: payments info
     * Get payments info
     *
     * @param xPagopaPnCxType    The type of the user
     * @param xPagopaPnCxId      The id of the user
     * @param paymentInfoRequest List of payments for which getting the additional info
     * @return the detailed list of payments
     */
    @Override
    public Mono<ResponseEntity<Flux<BffPaymentInfoItem>>> getPaymentsInfoV1(CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId,
                                                                            Flux<PaymentInfoRequest> paymentInfoRequest, final ServerWebExchange exchange) {

        Mono<List<BffPaymentInfoItem>> serviceResponse = paymentsService.getPaymentsInfo(xPagopaPnCxType, xPagopaPnCxId, paymentInfoRequest);

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(response)));
    }

    /**
     * POST bff/v1/payments/cart: payments cart
     * Get the cart url where to pay the payment
     *
     * @param paymentRequest Request to get the cart url where to pay the payment
     * @return the cart url
     */
    @Override
    public Mono<ResponseEntity<BffPaymentResponse>> paymentsCartV1(Mono<BffPaymentRequest> paymentRequest,
                                                                   final ServerWebExchange exchange) {

        Mono<BffPaymentResponse> serviceResponse = paymentsService.paymentsCart(paymentRequest);

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}