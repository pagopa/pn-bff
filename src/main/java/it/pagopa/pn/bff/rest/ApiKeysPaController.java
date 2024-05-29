package it.pagopa.pn.bff.rest;


import it.pagopa.pn.bff.generated.openapi.server.v1.api.ApiKeysApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.service.ApiKeysPaService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
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
    public Mono<ResponseEntity<BffApiKeysResponse>> getApiKeysV1(String xPagopaPnUid,
                                                                 CxTypeAuthFleet xPagopaPnCxType,
                                                                 String xPagopaPnCxId,
                                                                 List<String> xPagopaPnCxGroups,
                                                                 Integer limit,
                                                                 String lastKey,
                                                                 String lastUpdate,
                                                                 Boolean showVirtualKey,
                                                                 final ServerWebExchange exchange) {

        Mono<BffApiKeysResponse> serviceResponse = apiKeysPaService.getApiKeys(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxGroups, limit, lastKey, lastUpdate, showVirtualKey
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * POST bff/v1/api-keys: Create new api key
     * Create new api key that belongs to a Public Administration and are accessible by the current user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param exchange
     * @return the newly created api key
     */
    @Override
    public Mono<ResponseEntity<BffResponseNewApiKey>> newApiKeyV1(String xPagopaPnUid,
                                                                  CxTypeAuthFleet xPagopaPnCxType,
                                                                  String xPagopaPnCxId,
                                                                  Mono<BffRequestNewApiKey> bffRequestNewApiKey,
                                                                  List<String> xPagopaPnCxGroups,
                                                                  final ServerWebExchange exchange) {

        Mono<BffResponseNewApiKey> serviceResponse = apiKeysPaService.newApiKey(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, bffRequestNewApiKey, xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * DELETE bff/v1/api-keys/{id}: Delete an api key
     * Delete the api key identified by the id path parameter
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param id                ID of the api key to delete
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param exchange
     * @return
     */
    @Override
    public Mono<ResponseEntity<Void>> deleteApiKeyV1(String xPagopaPnUid,
                                                     CxTypeAuthFleet xPagopaPnCxType,
                                                     String xPagopaPnCxId,
                                                     String id,
                                                     List<String> xPagopaPnCxGroups,
                                                     final ServerWebExchange exchange) {

        Mono<Void> serviceResponse = apiKeysPaService.deleteApiKey(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, id, xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * PUT bff/v1/api-keys/{id}/status: Change status for an api key
     * Change the status of the api key identified by the id path parameter
     *
     * @param xPagopaPnUid           User Identifier
     * @param xPagopaPnCxType        Public Administration Type
     * @param xPagopaPnCxId          Public Administration id
     * @param id                     ID of the api key to change status
     * @param bffRequestApiKeyStatus New status for the api key
     * @param xPagopaPnCxGroups      Public Administration Group id List
     * @param exchange
     * @return
     */
    @Override
    public Mono<ResponseEntity<Void>> changeStatusApiKeyV1(String xPagopaPnUid,
                                                           CxTypeAuthFleet xPagopaPnCxType,
                                                           String xPagopaPnCxId,
                                                           String id,
                                                           Mono<BffRequestApiKeyStatus> bffRequestApiKeyStatus,
                                                           List<String> xPagopaPnCxGroups,
                                                           final ServerWebExchange exchange) {

        Mono<Void> serviceResponse = apiKeysPaService.changeStatusApiKey(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, id, bffRequestApiKeyStatus, xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}