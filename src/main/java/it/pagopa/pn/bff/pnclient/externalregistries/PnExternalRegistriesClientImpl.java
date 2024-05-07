package it.pagopa.pn.bff.pnclient.externalregistries;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.api.PaymentInfoApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoV21;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.*;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnExternalRegistriesClientImpl {
    private final InfoPaApi infoPaApi;
    private final PaymentInfoApi paymentInfoApi;

    public Flux<PaGroup> getGroups(String xPagopaPnUid,
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

    public Flux<InstitutionResourcePN> getInstitutions(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getInstitutions");
        return infoPaApi
                .getInstitutions(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, "WEB", xPagopaPnCxGroups, null);
    }

    public Flux<ProductResourcePN> getInstitutionProducts(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getInstitutionProducts");
        return infoPaApi
                .getInstitutionProducts(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, "WEB", xPagopaPnCxId, xPagopaPnCxGroups, null);
    }

    public Flux<PaymentInfoV21> getPaymentsInfo(List<PaymentInfoRequest> paymentInfoRequest) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getPaymentInfoV21");
        return paymentInfoApi
                .getPaymentInfoV21(paymentInfoRequest);
    }
}