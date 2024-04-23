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

    public Flux<InstitutionResourcePN> getInstitutions(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getInstitutions");
        return infoPaApi
                .getInstitutions(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, "WEB", xPagopaPnCxGroups, "")
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    public Flux<ProductResourcePN> getInstitutionProduct(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getInstitutionProduct");
        return infoPaApi
                .getInstitutionProducts(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, "WEB", xPagopaPnCxId, xPagopaPnCxGroups, "")
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}