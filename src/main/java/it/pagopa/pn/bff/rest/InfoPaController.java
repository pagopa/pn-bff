package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.InfoPaApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.*;
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

        Flux<BffInstitution> bffInstitutions = infoPaService
                .getInstitutions(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups);

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

        Flux<BffInstitutionProduct> bffInstitutionProducts = infoPaService
                .getInstitutionProducts(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups);

        return bffInstitutionProducts
                .collectList()
                .map(institutionProduct -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(institutionProduct)));
    }

    /**
     * GET bff/v1/pa/groups
     * Get the list of groups for the user
     *
     * @param xPagopaPnUid      (required)
     * @param xPagopaPnCxId     (required)
     * @param xPagopaPnCxGroups (required)
     * @param status            (optional)
     * @param exchange
     * @return the list of groups
     */
    @Override
    public Mono<ResponseEntity<Flux<BffPaGroup>>> getPAGroupsV1(String xPagopaPnUid, String xPagopaPnCxId, List<String> xPagopaPnCxGroups, BffPaGroupStatus status, ServerWebExchange exchange) {

        Flux<BffPaGroup> serviceResponse = infoPaService.getGroups(xPagopaPnUid, xPagopaPnCxId, xPagopaPnCxGroups, status);

        return serviceResponse
                .collectList()
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(response)));
    }

    /**
     * GET /bff/v1/pa/additional-lang
     * Get the list of additional languages for the PA
     *
     * @param xPagopaPnCxId (required)
     * @param exchange
     * @return the list of additional languages
     */
    @Override
    public Mono<ResponseEntity<BffAdditionalLanguages>> getAdditionalLang(String xPagopaPnCxId, final ServerWebExchange exchange) {

        Mono<BffAdditionalLanguages> serviceResponse = infoPaService.getLang(xPagopaPnCxId);

        return serviceResponse
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}