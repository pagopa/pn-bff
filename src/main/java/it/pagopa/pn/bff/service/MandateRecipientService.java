package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffMandatesCount;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.mandate.MandateCountMapper;
import it.pagopa.pn.bff.pnclient.mandate.PnMandateClientRecipientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MandateRecipientService {
    private final PnMandateClientRecipientImpl pnMandateClientRecipient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get total mandates based on required status if filter's specified.
     * If no filter is present, returns total of all pending and active mandates
     *
     * @param xPagopaPnCxId     User id
     * @param xPagopaPnCxType   User Type
     * @param xPagopaPnCxGroups User Group id List
     * @param xPagopaPnCxRole   User role
     * @param status            Mandate status
     * @return mandates count
     */
    public Mono<BffMandatesCount> countMandatesByDelegate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType,
                                                          List<String> xPagopaPnCxGroups, String xPagopaPnCxRole,
                                                          String status) {
        log.info("countMandatesByDelegate");
        return pnMandateClientRecipient
                .countMandatesByDelegate(xPagopaPnCxId, CxTypeMapper.cxTypeMapper.convertMandateCXType(xPagopaPnCxType), xPagopaPnCxGroups, xPagopaPnCxRole, status)
                .map(MandateCountMapper.modelMapper::mapCount)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

}