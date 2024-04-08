package it.pagopa.pn.bff.rest;


import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.ApiKeysApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.ApiKeysPaService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class ApiKeysPaController implements ApiKeysApi {
    private final ApiKeysPaService apiKeysPaService;

    public ApiKeysPaController(ApiKeysPaService apiKeysPaService) {
        this.apiKeysPaService = apiKeysPaService;
    }

    /**
     * GET bff/v1/api-keys: Api keys list
     * Get a paginated list of the api keys that belong to a Public Administration and are accessible by the current user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param limit             Number of items per page
     * @param lastKey           The last key returned by the previous search. If null, it will be returned the keys of the first page
     * @param lastUpdate        The update date of the last key returned by the previous search. If null, it will be returned the keys of the first page
     * @param showVirtualKey    Flag to show/hide the virtual key
     * @param exchange
     * @return the list of the api keys or error
     */
    @Override
    public Mono<ResponseEntity<BffApiKeysResponse>> getApiKeys(String xPagopaPnUid,
                                                               CxTypeAuthFleet xPagopaPnCxType,
                                                               String xPagopaPnCxId,
                                                               List<String> xPagopaPnCxGroups,
                                                               Integer limit,
                                                               String lastKey,
                                                               String lastUpdate,
                                                               Boolean showVirtualKey,
                                                               final ServerWebExchange exchange) {
        log.logStartingProcess("getApiKeys");

        Mono<BffApiKeysResponse> serviceResponse = apiKeysPaService.getApiKeys(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups, limit, lastKey, lastUpdate, showVirtualKey
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);


        log.logEndingProcess("getApiKeys");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}