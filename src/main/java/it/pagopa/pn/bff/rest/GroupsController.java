package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.GroupsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPaGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaGroup;
import it.pagopa.pn.bff.service.GroupsService;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class GroupsController implements GroupsApi {
    private final GroupsService groupsService;

    public GroupsController(GroupsService groupsService) {
        this.groupsService = groupsService;
    }

    /**
     * GET /v1/groups
     * Get the list of groups for the user
     *
     * @param xPagopaPnUid  (required)
     * @param xPagopaPnCxId  (required)
     * @param xPagopaPnCxGroups  (required)
     * @param statusFilter  (optional)
     * @param exchange
     * @return the list of groups
     */
    @Override
    public Mono<ResponseEntity<Flux<PaGroup>>> getGroupsV1(String xPagopaPnUid, String xPagopaPnCxId, List<String> xPagopaPnCxGroups, BffPaGroupStatus statusFilter, ServerWebExchange exchange) {
        log.logStartingProcess("getGroupsV1");

        Flux<PaGroup> serviceResponse = groupsService.getGroups(xPagopaPnUid, xPagopaPnCxId, xPagopaPnCxGroups, statusFilter);

        log.logEndingProcess("getGroupsV1");
        return Mono.just(ResponseEntity.ok(serviceResponse));
    }
}
