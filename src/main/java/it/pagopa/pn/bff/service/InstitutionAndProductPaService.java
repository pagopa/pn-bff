package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.InstitutionProductMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnInfoPaClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstitutionAndProductPaService {
    private final PnInfoPaClientImpl pnInfoPaClient;

    @Value("${pn.selfcare.baseurl}")
    private String selfcareUrl;

    @Value("${pn.selfcare.send.prod.id}")
    private String prodId;

    public Flux<BffInstitution> getInstitutions(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnSrcCh, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails) {
        log.info("getInstitutions");
        String pathTokenExchange = "/token-exchange?institutionId=";
        String pathProdId = "&productId=" + prodId;
        return pnInfoPaClient
                .getInstitutions(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(xPagopaPnCxType), xPagopaPnCxId, xPagopaPnSrcCh, xPagopaPnCxGroups, xPagopaPnSrcChDetails)
                .map(it.pagopa.pn.bff.mappers.InstitutionProductMapper.institutionProductMapper::toBffInstitution)
                .map(institution -> {
                    institution.setEntityUrl(selfcareUrl + pathTokenExchange + institution.getId() + pathProdId);
                    return institution;
                })
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    public Flux<BffInstitutionProduct> getInstitutionProducts(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnSrcCh, String institutionId, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails) {
        log.info("getInstitutionProducts");
        return pnInfoPaClient
                .getInstitutionProduct(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(xPagopaPnCxType), xPagopaPnCxId, xPagopaPnSrcCh, institutionId, xPagopaPnCxGroups, xPagopaPnSrcChDetails)
                .map(InstitutionProductMapper.institutionProductMapper::toBffInstitutionProduct)
                .map(product -> {
                    product.setProductUrl(selfcareUrl + "/token-exchange?institutionId=" + institutionId + "&productId=" + product.getId());
                    return product;
                })
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}
