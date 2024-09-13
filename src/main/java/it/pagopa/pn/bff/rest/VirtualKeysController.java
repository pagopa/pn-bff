package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestNewVirtualKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffRequestNewApiKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
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
public class VirtualKeysController {

    private final VirtualKeysService virtualKeysService;

    public VirtualKeysController(VirtualKeysService virtualKeysService){ this.virtualKeysService = virtualKeysService;}

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

}
