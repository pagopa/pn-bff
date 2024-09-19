package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoV21;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.payments.PaymentsCartMapper;
import it.pagopa.pn.bff.mappers.payments.PaymentsInfoMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentsService {

    private final PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get payments info.
     *
     * @param xPagopaPnCxType    The type of the user
     * @param xPagopaPnCxId      The id of the user
     * @param paymentInfoRequest List of payments for which getting the additional info
     * @return the detailed list of payments
     */
    public Mono<List<BffPaymentInfoItem>> getPaymentsInfo(CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, Flux<PaymentInfoRequest> paymentInfoRequest) {
        log.info("Get payment info - recipientId: {} - type: {}", xPagopaPnCxId, xPagopaPnCxType);

        return paymentInfoRequest.collectList().flatMap(request -> {
            Flux<PaymentInfoV21> paymentsInfo = pnExternalRegistriesClient.getPaymentsInfo(
                    CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(xPagopaPnCxType), xPagopaPnCxId,
                    PaymentsInfoMapper.modelMapper.mapPaymentInfoRequest(request)
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

            return paymentsInfo.map(PaymentsInfoMapper.modelMapper::mapPaymentInfoResponse).collectList();
        });
    }

    /**
     * Payments cart.
     *
     * @param paymentRequest Request to get the cart url where to pay the payment
     * @return the cart url
     */
    public Mono<BffPaymentResponse> paymentsCart(Mono<BffPaymentRequest> paymentRequest) {
        log.info("Get payment cart url");

        return paymentRequest.flatMap(request -> {
            Mono<PaymentResponse> paymentResponse = pnExternalRegistriesClient.paymentsCart(
                    PaymentsCartMapper.modelMapper.mapPaymentRequest(request)
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

            return paymentResponse.map(PaymentsCartMapper.modelMapper::mapPaymentResponse);
        });
    }
}