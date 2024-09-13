package it.pagopa.pn.bff.pnclient.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.api.VirtualKeysApi;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.*;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnVirtualKeysManagerClientPGImpl {
    private final VirtualKeysApi virtualKeysApi;

    public Mono<Void> deleteVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, String id, List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_APIKEY_MANAGER, "deleteVirtualKey");

        return virtualKeysApi.deleteVirtualKey(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, xPagopaPnCxRole, id, xPagopaPnCxGroups
        );
    }

    public Mono<ResponseNewVirtualKey> newVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                     String xPagopaPnCxId, String xPagopaPnCxRole , RequestNewVirtualKey requestNewVirtualKey,
                                                     List<String> xPagopaPnCxGroups){
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_APIKEY_MANAGER, "newVirtualKey");

        return virtualKeysApi.createVirtualKey(xPagopaPnUid,xPagopaPnCxType,xPagopaPnCxId,xPagopaPnCxRole, requestNewVirtualKey,xPagopaPnCxGroups);
    }
}
