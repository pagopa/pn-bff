package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.InstitutionAndProductApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.InstitutionAndProductPaService;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class InstitutionAndProductPaController implements InstitutionAndProductApi {

    private final InstitutionAndProductPaService institutionAndProductPaService;

    public InstitutionAndProductPaController(InstitutionAndProductPaService institutionAndProductPaService) {
        this.institutionAndProductPaService = institutionAndProductPaService;
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

        Flux<BffInstitution> bffInstitutions = institutionAndProductPaService
                .getInstitutions(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups);

        log.logEndingProcess("getInstitutionsV1");
        return bffInstitutions
                .collectList()
                .map(institution -> ResponseEntity.ok(Flux.fromIterable(institution)));
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

        Flux<BffInstitutionProduct> bffInstitutionProducts = institutionAndProductPaService
                .getInstitutionProducts(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups);

        log.logEndingProcess("getInstitutionProducts");
        return bffInstitutionProducts
                .collectList()
                .map(institutionProduct -> ResponseEntity.ok(Flux.fromIterable(institutionProduct)));
    }
}