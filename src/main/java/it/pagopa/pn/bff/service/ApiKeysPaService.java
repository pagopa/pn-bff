package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeysResponse;
import it.pagopa.pn.bff.mappers.apikeys.ApiKeysMapper;
import it.pagopa.pn.bff.pnclient.apikeys.PnApikeyManagerClientPAImpl;
import it.pagopa.pn.bff.pnclient.externalregistries.PnInfoPaClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeysPaService {
    private final PnApikeyManagerClientPAImpl pnApikeyManagerClientPA;
    private final PnInfoPaClientImpl pnInfoPaClient;

    public Mono<BffApiKeysResponse> getApiKeys(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                               String xPagopaPnCxId, List<String> xPagopaPnCxGroups,
                                               Integer limit, String lastKey,
                                               String lastUpdate, Boolean showVirtualKey
    ) {
        // list of api keys
        Mono<ApiKeysResponse> apiKeysResponse = pnApikeyManagerClientPA.getApiKeys(
                xPagopaPnUid,
                xPagopaPnCxType,
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
                .collect(Collectors.toList());

        return apiKeysResponse.map(res -> ApiKeysMapper.modelMapper.mapApiKeysResponse(res, paGroups));
    }
}