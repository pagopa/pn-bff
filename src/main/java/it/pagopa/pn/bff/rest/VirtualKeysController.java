package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestNewVirtualKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.VirtualKeysApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.*;
import it.pagopa.pn.bff.service.VirtualKeysService;
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

    private final VirtualKeysService virtualKeysService;

    public VirtualKeysController(VirtualKeysService virtualKeysService){ this.virtualKeysService = virtualKeysService;}

    /**
     * GET bff/v1/virtual-keys: Virtual keys list
     * Get a paginated list of the virtual
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
   // @Override
    public Mono<ResponseEntity<BffVirtualKeysResponse>> getVirtualKeysV1(String xPagopaPnUid,
                                                                 CxTypeAuthFleet xPagopaPnCxType,
                                                                 String xPagopaPnCxId,
                                                                 String xPagopaPnCxRole,
                                                                 List<String> xPagopaPnCxGroups,
                                                                 Integer limit,
                                                                 String lastKey,
                                                                 String lastUpdate,
                                                                 Boolean showVirtualKey,
                                                                 final ServerWebExchange exchange) {

        Mono<BffVirtualKeysResponse> serviceResponse = virtualKeysService.getVirtualKeys(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId,xPagopaPnCxRole, xPagopaPnCxGroups, limit, lastKey, lastUpdate, showVirtualKey
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    //@Override
    public Mono<ResponseEntity<Void>> deleteVirtualKey(String xPagopaPnUid, it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, String id, List<String> xPagopaPnCxGroups){
        Mono<Void> serviceResponse = virtualKeysService.deleteVirtualKey(xPagopaPnUid,xPagopaPnCxType,xPagopaPnCxId,xPagopaPnCxRole,id,xPagopaPnCxGroups);
        return  serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * Create new virtual key
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxRole    Public Administration role
     * @param requestNewVirtualKey   New virtual key request
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the newly created virtual key
     */

   // @Override
    public Mono<ResponseEntity<BffNewVirtualKeyResponse>> newVirtualKeyV1(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                          String xPagopaPnCxId, String xPagopaPnCxRole , Mono<BffNewVirtualKeyRequest> requestNewVirtualKey,
                                                                          List<String> xPagopaPnCxGroups) {

        Mono<BffNewVirtualKeyResponse> serviceResponse = virtualKeysService.newVirtualKey(xPagopaPnUid,xPagopaPnCxType,xPagopaPnCxId,xPagopaPnCxRole,requestNewVirtualKey,xPagopaPnCxGroups);

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * PUT bff/v1/virtual-keys/{id}/status: Change status for an virtual key
     * Change the status of the virtual key identified by the id path parameter
     *
     * @param xPagopaPnUid           User Identifier
     * @param xPagopaPnCxType        Public Administration Type
     * @param xPagopaPnCxId          Public Administration id
     * @param xPagopaPnCxRole        Public Administration role
     * @param id                     ID of the api key to change status
     * @param bffVirtualKeyStatusRequest The new virtual key status
     * @param xPagopaPnCxGroups      Public Administration Group id List
     * @return
     */
    //@Override
    public Mono<ResponseEntity<Void>> changeStatusVirtualKeyV1(String xPagopaPnUid, it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet xPagopaPnCxType,
                                                           String xPagopaPnCxId,String xPagopaPnCxRole, String id, Mono<BffVirtualKeyStatusRequest> bffVirtualKeyStatusRequest,
                                                           List<String> xPagopaPnCxGroups) {

        Mono<Void> serviceResponse = virtualKeysService.changeStatusVirtualKey(xPagopaPnUid,xPagopaPnCxType,xPagopaPnCxId,xPagopaPnCxRole,id,bffVirtualKeyStatusRequest,xPagopaPnCxGroups);

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}
