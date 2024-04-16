package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.PnBffConfigs;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.institutionandproduct.InstitutionMapper;
import it.pagopa.pn.bff.mappers.institutionandproduct.ProductMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnInfoPaClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstitutionAndProductPaService {
    private final PnInfoPaClientImpl pnInfoPaClient;
    private final PnBffConfigs pnBffConfigs;

    /**
     * Get a paginated list of the institutions that are accessible by the current user
     * @param xPagopaPnUid User Identifier
     * @param xPagopaPnCxType Public Administration Type
     * @param xPagopaPnCxId Public Administration id
     * @param xPagopaPnSrcCh Source Channel
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param xPagopaPnSrcChDetails Source Channel Details
     * @return the list of the institutions or error
     */
    public Flux<BffInstitution> getInstitutions(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnSrcCh, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails) {
        log.info("getInstitutions");
        String pathTokenExchange = "/token-exchange?institutionId=";
        String pathProdId = "&productId=" + pnBffConfigs.getSelfcareSendProdId();
        return pnInfoPaClient
                .getInstitutions(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(xPagopaPnCxType), xPagopaPnCxId, xPagopaPnSrcCh, xPagopaPnCxGroups, xPagopaPnSrcChDetails)
                .map(InstitutionMapper.INSTITUTION_MAPPER::toBffInstitution)
                .map(institution -> {
                    institution.setEntityUrl(pnBffConfigs.getSelfcareBaseUrl() + pathTokenExchange + institution.getId() + pathProdId);
                    return institution;
                })
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    /**
     * Get a paginated list of the products that belong to an institution and are accessible by the current user
     * @param xPagopaPnUid User Identifier
     * @param xPagopaPnCxType Public Administration Type
     * @param xPagopaPnCxId Public Administration id
     * @param xPagopaPnSrcCh Source Channel
     * @param institutionId Institution id
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param xPagopaPnSrcChDetails Source Channel Details
     * @return the list of the products or error
     */
    public Flux<BffInstitutionProduct> getInstitutionProducts(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnSrcCh, String institutionId, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails) {
        log.info("getInstitutionProducts");
        String pathTokenExchange = "/token-exchange?institutionId=";
        String pathProdId = "&productId=";
        return pnInfoPaClient
                .getInstitutionProduct(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(xPagopaPnCxType), xPagopaPnCxId, xPagopaPnSrcCh, institutionId, xPagopaPnCxGroups, xPagopaPnSrcChDetails)
                .map(ProductMapper.PRODUCT_MAPPER::toBffInstitutionProduct)
                .map(product -> {
                    product.setProductUrl(pnBffConfigs.getSelfcareBaseUrl() + pathTokenExchange + institutionId + pathProdId + product.getId());
                    return product;
                })
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}
