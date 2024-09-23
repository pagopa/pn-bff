package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.ResponseNewVirtualKey;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.VirtualKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffNewVirtualKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffNewVirtualKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffVirtualKeyStatusRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffVirtualKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;

import it.pagopa.pn.bff.mappers.virtualkeys.RequestNewVirtualKeysMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.RequestVirtualKeyStatusMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.ResponseNewVirtualKeysMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.VirtualKeysMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.pnclient.apikeys.PnVirtualKeysManagerClientPGImpl;
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
     * Get a paginated list of the virtual keys that belong to a Public Administration and are accessible by the current user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxRole     Public Administration role
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param limit             Number of items per page
     * @param lastKey           The last key returned by the previous search. If null, it will be returned the keys of the first page
     * @param lastUpdate        The update date of the last key returned by the previous search. If null, it will be returned the keys of the first page
     * @param showVirtualKey    Flag to show/hide the virtual key
     * @return the list of the api keys or error
     */
    public Mono<BffVirtualKeysResponse> getVirtualKeys(String xPagopaPnUid, it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet xPagopaPnCxType,
                                                       String xPagopaPnCxId, String xPagopaPnCxRole, List<String> xPagopaPnCxGroups,
                                                       Integer limit, String lastKey,
                                                       String lastUpdate, Boolean showVirtualKey
    ) {
        log.info("Get api key list - senderId: {} - type: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        Mono<VirtualKeysResponse> virtualKeysResponse = pnVirtualKeysManagerClientPG.getVirtualKeys(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertVirtualKeysPGCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                xPagopaPnCxRole,
                limit,
                lastKey,
                lastUpdate,
                showVirtualKey
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        // list of groups linked to the pa
        log.info("Get user groups - senderId: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxGroups);


        return virtualKeysResponse.map(VirtualKeysMapper.modelMapper::mapVirtualKeysResponse);
    }

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
    public Mono<Void> deleteVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String id, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole
    ){
       pnVirtualKeysManagerClientPG.deleteVirtualKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
               id,
               xPagopaPnCxGroups,
                xPagopaPnCxRole
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
    public Mono<BffNewVirtualKeyResponse> newVirtualKey(String xPagopaPnUid, it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet xPagopaPnCxType,
                                                        String xPagopaPnCxId , Mono<BffNewVirtualKeyRequest> requestNewVirtualKey,
                                                        List<String> xPagopaPnCxGroups, String xPagopaPnCxRole) {
        log.info("Create new virtual key - senderId: {} - type: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        return requestNewVirtualKey.flatMap(request -> {
            Mono<ResponseNewVirtualKey> responseNewVirtualKey = pnVirtualKeysManagerClientPG.newVirtualKey(
                    xPagopaPnUid,
                    xPagopaPnCxType,
                    xPagopaPnCxId,
                    RequestNewVirtualKeysMapper.modelMapper.mapRequestNewVirtualKey(request),
                    xPagopaPnCxGroups,
                    xPagopaPnCxRole
                    ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

            return responseNewVirtualKey.map(ResponseNewVirtualKeysMapper.modelMapper::mapResponseNewVirtualKey);
        });
    }

    /**
     * Change the status of an virtual key
     *
     * @param xPagopaPnUid           User Identifier
     * @param xPagopaPnCxType        Public Administration Type
     * @param xPagopaPnCxId          Public Administration id
     * @param xPagopaPnCxRole        Public Administration role
     * @param id                     ID of the api key to change status
     * @param bffVirtualKeyStatusRequest The new virtual key status
     * @param xPagopaPnCxGroups      Public Administration Group id List
     * @return
     */
    public Mono<Void> changeStatusVirtualKey(String xPagopaPnUid, it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet xPagopaPnCxType,
                                         String xPagopaPnCxId, String id, Mono<BffVirtualKeyStatusRequest> bffVirtualKeyStatusRequest,
                                         List<String> xPagopaPnCxGroups,String xPagopaPnCxRole) {
        log.info("Change api key {} status - senderId: {} - type: {} - groups: {}", id, xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);
        return bffVirtualKeyStatusRequest.flatMap(request ->
                pnVirtualKeysManagerClientPG.changeStatusVirtualKey(
                        xPagopaPnUid,
                        xPagopaPnCxType,
                        xPagopaPnCxId,
                        id,
                        RequestVirtualKeyStatusMapper.modelmapper.mapRequestVirtualKeyStatus(request),
                        xPagopaPnCxGroups ,
                        xPagopaPnCxRole
                ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
        );
    }
}
