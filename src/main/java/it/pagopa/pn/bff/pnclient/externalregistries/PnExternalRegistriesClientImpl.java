package it.pagopa.pn.bff.pnclient.externalregistries;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
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
public class PnExternalRegistriesClientImpl {
    private final InfoPaApi infoPaApi;

    public Flux<InstitutionResourcePN> getInstitutions(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnSrcCh, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getInstitutions");
        Flux<InstitutionResourcePN> institutions;
        institutions = infoPaApi
                .getInstitutions(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(xPagopaPnCxType), xPagopaPnCxId, xPagopaPnSrcCh, xPagopaPnCxGroups, xPagopaPnSrcChDetails)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
        return institutions;
    }

    public Flux<ProductResourcePN> getInstitutionProduct(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnSrcCh, String id, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES, "getInstitutionProduct");
        Flux<ProductResourcePN> productResourcePN;
        productResourcePN = infoPaApi
                .getInstitutionProducts(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(xPagopaPnCxType), xPagopaPnCxId, xPagopaPnSrcCh, id, xPagopaPnCxGroups, xPagopaPnSrcChDetails)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
        return productResourcePN;
    }
}
