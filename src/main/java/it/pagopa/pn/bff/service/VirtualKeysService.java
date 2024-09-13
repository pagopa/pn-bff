package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyResponse;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.*;
import it.pagopa.pn.bff.mappers.virtualkeys.RequestNewVirtualKeysMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.ResponseNewVirtualKeysMapper;
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
       pnVirtualKeysManagerClientPG.deleteVirtualKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                id,
                xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return Mono.empty();
    }

    /**
     * Create new virtual key
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxRole     Public Administration role
     * @param requestNewVirtualKey  Request that contains the name and the groups of te new api key
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the id and the value of the new virtual key
     */
    public Mono<BffNewVirtualKeyResponse> newVirtualKey(String xPagopaPnUid, it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet xPagopaPnCxType,
                                                        String xPagopaPnCxId, String xPagopaPnCxRole , Mono<BffNewVirtualKeyRequest> requestNewVirtualKey,
                                                        List<String> xPagopaPnCxGroups) {
        log.info("Create new virtual key - senderId: {} - type: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        return requestNewVirtualKey.flatMap(request -> {
            Mono<ResponseNewVirtualKey> responseNewVirtualKey = pnVirtualKeysManagerClientPG.newVirtualKey(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertVirtualKeysPGCXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    xPagopaPnCxRole,
                    RequestNewVirtualKeysMapper.modelMapper.mapRequestNewVirtualKey(request),
                    xPagopaPnCxGroups
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

            return responseNewVirtualKey.map(ResponseNewVirtualKeysMapper.modelMapper::mapResponseNewVirtualKey);
        });
    }
}
