package it.pagopa.pn.bff.pnclient.apikeys;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.api.ApiKeysApi;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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

        Mono<ApiKeysResponse> apiKeysResponse;
        apiKeysResponse = apiKeysApi.getApiKeys(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                limit,
                lastKey,
                lastUpdate,
                showVirtualKey
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return apiKeysResponse;
    }
}