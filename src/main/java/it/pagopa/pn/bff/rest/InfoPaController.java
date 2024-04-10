package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.InstitutionProductsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.InstitutionsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.InfoPaService;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class InfoPaController implements InstitutionProductsApi, InstitutionsApi {

    private final InfoPaService infoPaService;

    public InfoPaController(InfoPaService infoPaService) {
        this.infoPaService = infoPaService;
    }

    /**
     * GET /v1/institutions
     * Get the list of institutions
     *
     * @param xPagopaPnUid          The user id
     * @param xPagopaPnCxType       The type of the user
     * @param xPagopaPnCxId         The id of the user
     * @param xPagopaPnSrcCh        The source channel
     * @param xPagopaPnCxGroups     The groups of the user
     * @param xPagopaPnSrcChDetails The details of the source channel
     * @return The list of institutions
     */
    @Override
    public Mono<ResponseEntity<Flux<BffInstitution>>> getInstitutionsV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnSrcCh, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails, ServerWebExchange exchange) {
        log.logStartingProcess("getInstitutesV1");
        Flux<BffInstitution> bffInstitutions = infoPaService
                .getInstitutions(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnSrcCh, xPagopaPnCxGroups, xPagopaPnSrcChDetails)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
        log.logEndingProcess("getInstitutesV1");
        return bffInstitutions
                .collectList()
                .map(institution -> ResponseEntity.ok(Flux.fromIterable(institution)));
    }

    /**
     * GET /v1/institutions/{institutionId}/products
     * Get the list of products of an institution
     *
     * @param xPagopaPnUid          The user id
     * @param xPagopaPnCxType       The type of the user
     * @param xPagopaPnCxId         The id of the user
     * @param xPagopaPnSrcCh        The source channel
     * @param institutionId         The id of the institution (path variable)
     * @param xPagopaPnCxGroups     The groups of the user
     * @param xPagopaPnSrcChDetails The details of the source channel
     * @return The list of products of an institution
     */
    @Override
    public Mono<ResponseEntity<Flux<BffInstitutionProduct>>> getInstitutionProductsV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnSrcCh, String institutionId, List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails, ServerWebExchange exchange) {
        log.logStartingProcess("getInstitutionProductsV1");
        Flux<BffInstitutionProduct> bffInstitutionProducts = infoPaService
                .getInstitutionProducts(xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnSrcCh, institutionId, xPagopaPnCxGroups, xPagopaPnSrcChDetails)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
        log.logEndingProcess("getInstitutionProductsV1");
        return bffInstitutionProducts
                .collectList()
                .map(institutionProduct -> ResponseEntity.ok(Flux.fromIterable(institutionProduct)));
    }
}
