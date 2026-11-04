package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtDelegationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtInstitutionsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.ptinstitutions.PtInstitutionsMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PtInstitutionsService {
    private final PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get a paginated list of the institutions that delegated the technology partner
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Technology partner Type
     * @param xPagopaPnCxId   Technology partner id
     * @param search          Filter on institution name or tax code
     * @param status          Filter on delegation status
     * @param limit           Page size
     * @param lastKey         Key of the page to read
     * @return the paginated list of institutions or error
     */
    public Mono<BffPtInstitutionsResponse> getPtInstitutions(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId,
                                                             String search, BffPtDelegationStatus status, Integer limit, String lastKey) {
        log.info("Get pt institutions - ptId: {} - type: {} - status: {} - limit: {}", xPagopaPnCxId, xPagopaPnCxType, status, limit);

        return pnExternalRegistriesClient
                .getPtInstitutions(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertExternalRegistriesPtCXType(xPagopaPnCxType),
                        xPagopaPnCxId,
                        search,
                        PtInstitutionsMapper.modelMapper.toPtDelegationStatus(status),
                        limit,
                        lastKey)
                .map(PtInstitutionsMapper.modelMapper::toBffPtInstitutionsResponse)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }
}