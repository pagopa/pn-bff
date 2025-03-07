package it.pagopa.pn.bff.pnclient.emd;

import it.pagopa.pn.bff.generated.openapi.msclient.emd.api.CheckTppApi;
import it.pagopa.pn.bff.generated.openapi.msclient.emd.api.PaymentApi;
import it.pagopa.pn.bff.generated.openapi.msclient.emd.model.PaymentUrlResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.emd.model.RetrievalPayload;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnEmdClientImpl {

    private final CheckTppApi checkTppApi;
    private final PaymentApi paymentApi;

    public Mono<RetrievalPayload> checkTpp(String retrievalId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EMD_INTEGRATION, "checkTpp");

        return checkTppApi.emdCheckTPP(retrievalId);
    }

    public Mono<PaymentUrlResponse> getPaymentUrl(String retrievalId, String noticeCode, String paTaxId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EMD_INTEGRATION, "getPaymentUrl");

        return paymentApi.getPaymentUrl(retrievalId, noticeCode, paTaxId);
    }
}