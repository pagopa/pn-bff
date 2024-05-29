package it.pagopa.pn.bff.pnclient.apikeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.api.ApiKeysApi;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.*;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnApikeyManagerClientPAImpl {

    private final ApiKeysApi apiKeysApi;

    public Mono<ApiKeysResponse> getApiKeys(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                            String xPagopaPnCxId, List<String> xPagopaPnCxGroups,
                                            Integer limit, String lastKey,
                                            String lastUpdate, Boolean showVirtualKey) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_APIKEY_MANAGER, "getApiKeys");

        return apiKeysApi.getApiKeys(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                limit,
                lastKey,
                lastUpdate,
                showVirtualKey
        );
    }

    public Mono<ResponseNewApiKey> newApiKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                             String xPagopaPnCxId, RequestNewApiKey requestNewApiKey,
                                             List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_APIKEY_MANAGER, "newApiKey");

        return apiKeysApi.newApiKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                requestNewApiKey,
                xPagopaPnCxGroups
        );
    }

    public Mono<Void> deleteApiKeys(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                    String xPagopaPnCxId, String id,
                                    List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_APIKEY_MANAGER, "deleteApiKeys");

        return apiKeysApi.deleteApiKeys(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                id,
                xPagopaPnCxGroups
        );
    }

    public Mono<Void> changeStatusApiKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                         String xPagopaPnCxId, String id, RequestApiKeyStatus requestApiKeyStatus,
                                         List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_APIKEY_MANAGER, "changeStatusApiKey");

        return apiKeysApi.changeStatusApiKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                id,
                requestApiKeyStatus,
                xPagopaPnCxGroups
        );
    }
}