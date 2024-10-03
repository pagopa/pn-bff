package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.VirtualKeysApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.*;
import it.pagopa.pn.bff.service.VirtualKeysPgService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class VirtualKeysController implements VirtualKeysApi {

    private final VirtualKeysPgService virtualKeysPgService;

    public VirtualKeysController(VirtualKeysPgService virtualKeysPgService) {
        this.virtualKeysPgService = virtualKeysPgService;
    }

    /**
     * GET bff/v1/pg/virtual-keys: Virtual keys list
     * Get a paginated list of the virtual keys
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Customer/Recipient Type
     * @param xPagopaPnCxId     Customer/Recipient id
     * @param xPagopaPnCxGroups Customer/Recipient id List
     * @param limit             Number of items per page
     * @param lastKey           The last key returned by the previous search. If null, it will be returned the keys of the first page
     * @param lastUpdate        The update date of the last key returned by the previous search. If null, it will be returned the keys of the first page
     * @param showVirtualKey    Flag to show/hide the virtual key
     * @param exchange
     * @return the list of the virtual keys or error
     */
    @Override
    public Mono<ResponseEntity<BffVirtualKeysResponse>> getVirtualKeysV1(String xPagopaPnUid,
                                                                         CxTypeAuthFleet xPagopaPnCxType,
                                                                         String xPagopaPnCxId,
                                                                         List<String> xPagopaPnCxGroups,
                                                                         String xPagopaPnCxRole,
                                                                         Integer limit,
                                                                         String lastKey,
                                                                         String lastUpdate,
                                                                         Boolean showVirtualKey,
                                                                         final ServerWebExchange exchange) {

        Mono<BffVirtualKeysResponse> serviceResponse = virtualKeysPgService.getVirtualKeys(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxRole, xPagopaPnCxGroups, limit, lastKey, lastUpdate, showVirtualKey
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * DELETE bff/v1/pg/virtual-keys/{kid}: delete virtual key
     * Delete virtual key with passing kid
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Customer/Recipient Type
     * @param xPagopaPnCxId     Customer/Recipient id
     * @param kid               kid
     * @param xPagopaPnCxRole   Customer/Recipient role
     * @param xPagopaPnCxGroups Customer/Recipient Group id List
     * @return Void
     */
    @Override
    public Mono<ResponseEntity<Void>> deleteVirtualKeyV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                         String xPagopaPnCxId, String kid,
                                                         List<String> xPagopaPnCxGroups, String xPagopaPnCxRole,
                                                         final ServerWebExchange exchange) {
        Mono<Void> serviceResponse = virtualKeysPgService.deleteVirtualKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                kid,
                xPagopaPnCxGroups,
                xPagopaPnCxRole
        );
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * POST bff/v1/pg/virtual-keys: create a new virtual key
     * Create new virtual key
     *
     * @param xPagopaPnUid         User Identifier
     * @param xPagopaPnCxType      Customer/Recipientn Type
     * @param xPagopaPnCxId        Customer/Recipient id
     * @param xPagopaPnCxRole      Customer/Recipient role
     * @param requestNewVirtualKey New virtual key request
     * @param xPagopaPnCxGroups    Customer/Recipient Group id List
     * @return the newly created virtual key
     */

    @Override
    public Mono<ResponseEntity<BffNewVirtualKeyResponse>> newVirtualKeyV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                          String xPagopaPnCxId, Mono<BffNewVirtualKeyRequest> requestNewVirtualKey,
                                                                          List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, ServerWebExchange exchange) {

        Mono<BffNewVirtualKeyResponse> serviceResponse = virtualKeysPgService.newVirtualKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                requestNewVirtualKey,
                xPagopaPnCxGroups,
                xPagopaPnCxRole
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * PUT bff/v1/pg/virtual-keys/{kid}/status: Change status for an virtual key
     * Change the status of the virtual key identified by the id path parameter
     *
     * @param xPagopaPnUid               User Identifier
     * @param xPagopaPnCxType            Customer/Recipient Type
     * @param xPagopaPnCxId              Customer/Recipient id
     * @param xPagopaPnCxRole            Customer/Recipient role
     * @param kid                        kid of the api key to change status
     * @param bffVirtualKeyStatusRequest The new virtual key status
     * @param xPagopaPnCxGroups          Customer/Recipient id List
     * @return void
     */
    @Override
    public Mono<ResponseEntity<Void>> changeStatusVirtualKeysV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                String xPagopaPnCxId, String kid, Mono<BffVirtualKeyStatusRequest> bffVirtualKeyStatusRequest,
                                                                List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, ServerWebExchange exchange) {

        Mono<Void> serviceResponse = virtualKeysPgService.changeStatusVirtualKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                kid,
                bffVirtualKeyStatusRequest,
                xPagopaPnCxGroups,
                xPagopaPnCxRole
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}