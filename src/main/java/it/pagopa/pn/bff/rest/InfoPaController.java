package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.service.InfoPaService;
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
public class InfoPaController implements InfoPaApi {

    private final InfoPaService infoPaService;

    public InfoPaController(InfoPaService infoPaService) {
        this.infoPaService = infoPaService;
    }

    /**
     * GET bff/v1/institutions
     * Get the list of institutions
     *
     * @param xPagopaPnUid      The user id
     * @param xPagopaPnCxType   The type of the user
     * @param xPagopaPnCxId     The id of the user
     * @param xPagopaPnCxGroups The groups of the user
     * @return The list of institutions
     */
    @Override
    public Mono<ResponseEntity<Flux<BffInstitution>>> getInstitutionsV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups, ServerWebExchange exchange) {
        log.logStartingProcess("getInstitutionsV1");

        Flux<BffInstitution> bffInstitutions = infoPaService
                .getInstitutions(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups);

        log.logEndingProcess("getInstitutionsV1");
        return bffInstitutions
                .collectList()
                .map(institution -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(institution)));
    }

    /**
     * GET bff/v1/institutions/products
     * Get the list of products of an institution
     *
     * @param xPagopaPnUid      The user id
     * @param xPagopaPnCxType   The type of the user
     * @param xPagopaPnCxId     The id of the user
     * @param xPagopaPnCxGroups The groups of the user
     * @return The list of products of an institution
     */
    @Override
    public Mono<ResponseEntity<Flux<BffInstitutionProduct>>> getInstitutionProductsV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups, ServerWebExchange exchange) {
        log.logStartingProcess("getInstitutionProducts");

        Flux<BffInstitutionProduct> bffInstitutionProducts = infoPaService
                .getInstitutionProducts(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups);

        log.logEndingProcess("getInstitutionProducts");
        return bffInstitutionProducts
                .collectList()
                .map(institutionProduct -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(institutionProduct)));
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
    public Mono<ResponseEntity<Flux<BffPaGroup>>> getGroupsV1(String xPagopaPnUid, String xPagopaPnCxId, List<String> xPagopaPnCxGroups, BffPaGroupStatus statusFilter, ServerWebExchange exchange) {
        log.logStartingProcess("getGroupsV1");

        Flux<BffPaGroup> serviceResponse = infoPaService.getGroups(xPagopaPnUid, xPagopaPnCxId, xPagopaPnCxGroups, statusFilter);

        log.logEndingProcess("getGroupsV1");
        return Mono.just(ResponseEntity.ok(serviceResponse));
    }
}