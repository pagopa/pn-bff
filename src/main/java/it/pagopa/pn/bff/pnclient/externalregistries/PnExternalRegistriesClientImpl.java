package it.pagopa.pn.bff.pnclient.externalregistries;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.api.PaymentInfoApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoV21;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPgApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.*;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnExternalRegistriesClientImpl {
    private final InfoPaApi infoPaApi;
    private final InfoPgApi infoPgApi;
    private final PaymentInfoApi paymentInfoApi;

    public Flux<PaGroup> getPaGroups(String xPagopaPnUid,
                                     String xPagopaPnCxId, List<String> xPagopaPnCxGroups,
                                     PaGroupStatus paGroupStatus) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getGroups");

        return infoPaApi.getGroups(
                xPagopaPnUid,
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                paGroupStatus
        );
    }

    public Flux<PgGroup> getPgGroups(String xPagopaPnUid,
                                     String xPagopaPnCxId, List<String> xPagopaPnCxGroups,
                                     PgGroupStatus pgGroupStatus) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getPgGroups");
        return infoPgApi.getPgGroups(
                xPagopaPnUid,
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                pgGroupStatus
        );
    }

    public Flux<PaSummary> getPaList(String paNameFilter) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "listOnboardedPa");
        return infoPaApi.listOnboardedPa(paNameFilter, null);
    }

    public Flux<InstitutionResourcePN> getInstitutions(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getUserInstitutions");
        return infoPaApi
                .getUserInstitutions(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, "WEB", xPagopaPnCxGroups, null);
    }

    public Flux<ProductResourcePN> getInstitutionProducts(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getInstitutionProducts");
        return infoPaApi
                .getInstitutionProducts(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, "WEB", xPagopaPnCxId, xPagopaPnCxGroups, null);
    }

    public Flux<PaymentInfoV21> getPaymentsInfo(CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<PaymentInfoRequest> paymentInfoRequest) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getPaymentInfoV21");
        // verify the required parameter 'xPagopaPnCxType' is set
        if (xPagopaPnCxType == null) {
            throw new WebClientResponseException("Missing the required parameter 'xPagopaPnCxType' when calling searchReceivedNotification", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        // verify the required parameter 'xPagopaPnCxId' is set
        if (xPagopaPnCxId == null) {
            throw new WebClientResponseException("Missing the required parameter 'xPagopaPnCxId' when calling searchReceivedNotification", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null, null, null);
        }
        return paymentInfoApi
                .getPaymentInfoV21(paymentInfoRequest);
    }

    public Mono<PaymentResponse> paymentsCart(PaymentRequest paymentRequest) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "checkoutCart");
        return paymentInfoApi
                .checkoutCart(paymentRequest);
    }


}