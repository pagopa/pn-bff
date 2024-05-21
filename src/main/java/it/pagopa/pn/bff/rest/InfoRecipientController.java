package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.InfoRecipientApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPaSummary;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroupStatus;
import it.pagopa.pn.bff.service.InfoRecipientService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class InfoRecipientController implements InfoRecipientApi {
    private final InfoRecipientService infoRecipientService;

    public InfoRecipientController(InfoRecipientService infoRecipientService) {
        this.infoRecipientService = infoRecipientService;
    }

    /**
     * GET bff/v1/pg/groups
     * Get the list of PG groups
     *
     * @param xPagopaPnUid      The user id
     * @param xPagopaPnCxId     The id of the user
     * @param xPagopaPnCxGroups The groups of the user
     * @param status            The status of the PG group
     * @return The list of PG groups
     */
    @Override
    public Mono<ResponseEntity<Flux<BffPgGroup>>> getPGGroupsV1(String xPagopaPnUid, String xPagopaPnCxId, List<String> xPagopaPnCxGroups, BffPgGroupStatus status, ServerWebExchange exchange) {
        log.logStartingProcess("getPGGroupsV1");

        Flux<BffPgGroup> bffPgGroups = infoRecipientService.getGroups(xPagopaPnUid, xPagopaPnCxId, xPagopaPnCxGroups, status);

        log.logEndingProcess("getPGGroupsV1");
        return bffPgGroups.collectList().map(groups -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(groups)));
    }

    /**
     * GET bff/v1/pa-list
     * Get the list of PA that use PN
     *
     * @param paNameFilter The prefix of the PA name
     * @param id           The id of the PA
     * @return The list of PA
     */
    @Override
    public Mono<ResponseEntity<Flux<BffPaSummary>>> getPAListV1(String paNameFilter,
                                                                List<String> id,
                                                                final ServerWebExchange exchange) {
        log.logStartingProcess("getPAListV1");

        Flux<BffPaSummary> paSummaryFlux = infoRecipientService.getPaList(paNameFilter, id);

        log.logEndingProcess("getPAListV1");
        return paSummaryFlux.collectList().map(paSummaries -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(paSummaries)));
    }
}