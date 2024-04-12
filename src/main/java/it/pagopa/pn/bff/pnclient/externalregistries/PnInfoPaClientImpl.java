package it.pagopa.pn.bff.pnclient.externalregistries;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.*;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnInfoPaClientImpl {
    private final InfoPaApi infoPaApi;
    public Flux<PaGroup> getGroups(String xPagopaPnUid,
                                   String xPagopaPnCxId, List<String> xPagopaPnCxGroups,
                                   PaGroupStatus paGroupStatus) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getGroups");

        return infoPaApi.getGroups(
                xPagopaPnUid,
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                paGroupStatus
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    public Flux<InstitutionResourcePN> getInstitutions(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnSrcCh, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getInstitutions");
        Flux<InstitutionResourcePN> institutions;
        institutions = infoPaApi
                .getInstitutions(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnSrcCh, xPagopaPnCxGroups, xPagopaPnSrcChDetails)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
        return institutions;
    }

    public Flux<ProductResourcePN> getInstitutionProduct(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnSrcCh, String id, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getInstitutionProduct");
        Flux<ProductResourcePN> productResourcePN;
        productResourcePN = infoPaApi
                .getInstitutionProducts(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnSrcCh, id, xPagopaPnCxGroups, xPagopaPnSrcChDetails)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
        return productResourcePN;
    }
}