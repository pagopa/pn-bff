package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.apikeys.ApiKeysMapper;
import it.pagopa.pn.bff.pnclient.apikeys.PnApikeyManagerClientPAImpl;
import it.pagopa.pn.bff.pnclient.externalregistries.PnInfoPaClientImpl;
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
    private final PnInfoPaClientImpl pnInfoPaClient;

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
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        // list of groups linked to the pa
        List<PaGroup> paGroups = pnInfoPaClient.getGroups(
                        xPagopaPnUid,
                        xPagopaPnCxId,
                        xPagopaPnCxGroups,
                        null
                )
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException)
                .toStream()
                .toList();

        return apiKeysResponse.map(res -> ApiKeysMapper.modelMapper.mapApiKeysResponse(res, paGroups));
    }
}