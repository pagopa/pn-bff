package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.infopa.GroupsMapper;
import it.pagopa.pn.bff.mappers.infopa.InstitutionMapper;
import it.pagopa.pn.bff.mappers.infopa.ProductMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoPaService {
    private final PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private final PnBffConfigs pnBffConfigs;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get a paginated list of the institutions that are accessible by the current user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the list of the institutions or error
     */
    public Flux<BffInstitution> getInstitutions(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups) {
        log.info("getInstitutions");
        return pnExternalRegistriesClient
                .getInstitutions(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(xPagopaPnCxType), xPagopaPnCxId, xPagopaPnCxGroups)
                .map(institution -> InstitutionMapper.modelMapper.toBffInstitution(institution, pnBffConfigs))
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Get a paginated list of the products that belong to an institution and are accessible by the current user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the list of the products or error
     */
    public Flux<BffInstitutionProduct> getInstitutionProducts(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups) {
        log.info("getInstitutionProducts");
        return pnExternalRegistriesClient
                .getInstitutionProducts(xPagopaPnUid, CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(xPagopaPnCxType), xPagopaPnCxId, xPagopaPnCxGroups)
                .map(product -> ProductMapper.modelMapper.toBffInstitutionProduct(product, pnBffConfigs, xPagopaPnCxId))
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Get the list of groups for the user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxGroups  Public Administration Group id List
     * @param statusFilter    Filter for the status of the groups
     * @return the list of groups
     */
    public Flux<PaGroup> getGroups(String xPagopaPnUid, String xPagopaPnCxId, List<String> xPagopaPnCxGroups, BffPaGroupStatus statusFilter){
        log.info("getGroups");

        Flux<it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup> paGroups = pnExternalRegistriesClient.getGroups(
                xPagopaPnUid,
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                null
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return paGroups.map(GroupsMapper.modelMapper::mapGroups);
    }
}