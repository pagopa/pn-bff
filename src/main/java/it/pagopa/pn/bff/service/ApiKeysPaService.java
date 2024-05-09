package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ResponseNewApiKey;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.apikeys.ApiKeysMapper;
import it.pagopa.pn.bff.mappers.apikeys.RequestApiKeyStatusMapper;
import it.pagopa.pn.bff.mappers.apikeys.RequestNewApiKeyMapper;
import it.pagopa.pn.bff.mappers.apikeys.ResponseNewApiKeyMapper;
import it.pagopa.pn.bff.pnclient.apikeys.PnApikeyManagerClientPAImpl;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeysPaService {
    private final PnApikeyManagerClientPAImpl pnApikeyManagerClientPA;
    private final PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
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
     * @return the list of the api keys or error
     */
    public Mono<BffApiKeysResponse> getApiKeys(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                               String xPagopaPnCxId, List<String> xPagopaPnCxGroups,
                                               Integer limit, String lastKey,
                                               String lastUpdate, Boolean showVirtualKey
    ) {
        log.info("Get api key list");
        // list of api keys
        Mono<ApiKeysResponse> apiKeysResponse = pnApikeyManagerClientPA.getApiKeys(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertApiKeysPACXType(xPagopaPnCxType),
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                limit,
                lastKey,
                lastUpdate,
                showVirtualKey
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        // list of groups linked to the pa
        Mono<List<PaGroup>> paGroups = pnExternalRegistriesClient.getGroups(
                        xPagopaPnUid,
                        xPagopaPnCxId,
                        xPagopaPnCxGroups,
                        null
                )
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
                .collectList();

        return Mono.zip(apiKeysResponse, paGroups).map(res -> ApiKeysMapper.modelMapper.mapApiKeysResponse(res.getT1(), res.getT2()));
    }

    /**
     * Create new api key
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param requestNewApiKey  Request that contains the name and the groups of te new api key
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the id and the value of the new api key
     */
    public Mono<BffResponseNewApiKey> newApiKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                String xPagopaPnCxId, Mono<BffRequestNewApiKey> requestNewApiKey,
                                                List<String> xPagopaPnCxGroups) {
        log.info("Create new api key");
        return requestNewApiKey.flatMap(request -> {
            Mono<ResponseNewApiKey> responseNewApiKey = pnApikeyManagerClientPA.newApiKey(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertApiKeysPACXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    RequestNewApiKeyMapper.modelMapper.mapRequestNewApiKey(request),
                    xPagopaPnCxGroups
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

            return responseNewApiKey.map(ResponseNewApiKeyMapper.modelMapper::mapResponseNewApiKey);
        });
    }

    /**
     * Delete an api key
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param id                ID of the api key to delete
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return
     */
    public Mono<Void> deleteApiKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                   String xPagopaPnCxId, String id,
                                   List<String> xPagopaPnCxGroups) {
        log.info("Delete api key");
        return pnApikeyManagerClientPA.deleteApiKeys(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertApiKeysPACXType(xPagopaPnCxType),
                xPagopaPnCxId,
                id,
                xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Change the status of an api key
     *
     * @param xPagopaPnUid           User Identifier
     * @param xPagopaPnCxType        Public Administration Type
     * @param xPagopaPnCxId          Public Administration id
     * @param id                     ID of the api key to change status
     * @param bffRequestApiKeyStatus The new api key status
     * @param xPagopaPnCxGroups      Public Administration Group id List
     * @return
     */
    public Mono<Void> changeStatusApiKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                         String xPagopaPnCxId, String id, Mono<BffRequestApiKeyStatus> bffRequestApiKeyStatus,
                                         List<String> xPagopaPnCxGroups) {
        log.info("Change api key status");
        return bffRequestApiKeyStatus.flatMap(request ->
                pnApikeyManagerClientPA.changeStatusApiKey(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertApiKeysPACXType(xPagopaPnCxType),
                        xPagopaPnCxId,
                        id,
                        RequestApiKeyStatusMapper.modelMapper.mapRequestApiKeyStatus(request),
                        xPagopaPnCxGroups
                ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
        );
    }
}