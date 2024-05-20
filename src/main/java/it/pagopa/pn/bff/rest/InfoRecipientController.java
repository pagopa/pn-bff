package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.InfoRecipientApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroupStatus;
import it.pagopa.pn.bff.service.InfoRecipientService;
import lombok.CustomLog;
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
        return Mono.just(ResponseEntity.ok(bffPgGroups));
    }
}
