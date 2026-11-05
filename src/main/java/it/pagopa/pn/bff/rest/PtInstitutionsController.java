package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.PtInstitutionsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtDelegationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.BffPtInstitutionsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.pt_institutions.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.PtInstitutionsService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@CustomLog
@RestController
public class PtInstitutionsController implements PtInstitutionsApi {

    private final PtInstitutionsService ptInstitutionsService;

    public PtInstitutionsController(PtInstitutionsService ptInstitutionsService) {
        this.ptInstitutionsService = ptInstitutionsService;
    }

    /**
     * GET bff/v1/pt/institutions
     * Get the paginated list of the institutions that delegated the technology partner
     *
     * @return the paginated list of institutions
     */
    @Override
    public Mono<ResponseEntity<BffPtInstitutionsResponse>> getPtInstitutionsV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId,
                                                                               String search, BffPtDelegationStatus status, Integer limit, String lastKey,
                                                                               final ServerWebExchange exchange) {
        return ptInstitutionsService
                .getPtInstitutions(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, search, status, limit, lastKey)
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}