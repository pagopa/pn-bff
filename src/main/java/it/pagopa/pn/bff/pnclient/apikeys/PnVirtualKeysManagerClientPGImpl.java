package it.pagopa.pn.bff.pnclient.apikeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.api.VirtualKeysApi;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
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

    public Mono<VirtualKeysResponse> getVirtualKeys(String xPagopaPnUid, it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.CxTypeAuthFleet xPagopaPnCxType,
                                            String xPagopaPnCxId, List<String> xPagopaPnCxGroups,String xPagopaPnCxRole,
                                            Integer limit, String lastKey,
                                            String lastUpdate, Boolean showVirtualKey) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_APIKEY_MANAGER, "getVirtualKeys");

        return virtualKeysApi.getVirtualKeys(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                xPagopaPnCxGroups,
                limit,
                lastKey,
                lastUpdate,
                showVirtualKey
        );
    }

    public Mono<Void> deleteVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                       String xPagopaPnCxId, String id,
                                       List<String> xPagopaPnCxGroups, String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_APIKEY_MANAGER, "deleteVirtualKey");

        return virtualKeysApi.deleteVirtualKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                id,
                xPagopaPnCxGroups
        );
    }

    public Mono<ResponseNewVirtualKey> newVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                     String xPagopaPnCxId , RequestNewVirtualKey requestNewVirtualKey,
                                                     List<String> xPagopaPnCxGroups, String xPagopaPnCxRole){
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_APIKEY_MANAGER, "newVirtualKey");

        return virtualKeysApi.createVirtualKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                requestNewVirtualKey,
                xPagopaPnCxGroups
        );
    }

    public Mono<Void> changeStatusVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                         String xPagopaPnCxId, String id, RequestVirtualKeyStatus requestVirtualKeyStatus,
                                         List<String> xPagopaPnCxGroups,String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_APIKEY_MANAGER, "changeStatusVirtualKey");

        return virtualKeysApi.changeStatusVirtualKeys(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                id,
                requestVirtualKeyStatus,
                xPagopaPnCxGroups
        );
    }
}
