package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.virtualkey.apikey_pg.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.VirtualKeysService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class VirtualKeysController {

    private final VirtualKeysService virtualKeysService;

    public VirtualKeysController(VirtualKeysService virtualKeysService){ this.virtualKeysService = virtualKeysService;}

    public Mono<ResponseEntity<Void>> deleteVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, String id, List<String> xPagopaPnCxGroups){
        Mono<Void> serviceResponse = virtualKeysService.deleteVirtualKey(xPagopaPnUid,xPagopaPnCxType,xPagopaPnCxId,xPagopaPnCxRole,id,xPagopaPnCxGroups);
        return  serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}
