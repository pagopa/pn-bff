package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.virtualkey.apikey_pg.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.pnclient.virtualkeys.PnVirtualKeysManagerClientPGImpl;
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
public class VirtualKeysService {

    private final PnVirtualKeysManagerClientPGImpl pnVirtualKeysManagerClientPG;
    private final PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get a paginated list of the api keys that belong to a Public Administration and are accessible by the current user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxRole     Public Administration Role
     * @param xPagopaPnCxId     Public Administration id
     * @param id                Virtual Keys id
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return
     */
    public Mono<Void> deleteVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, String id, List<String> xPagopaPnCxGroups
    ){
        Mono<Void> virtualKeyResponse = pnVirtualKeysManagerClientPG.deleteVirtualKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
               xPagopaPnCxRole,
                id,
                xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return Mono.empty();
    }
}
