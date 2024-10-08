package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.ResponseNewVirtualKey;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.VirtualKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.RequestNewVirtualKeysMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.RequestVirtualKeyStatusMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.ResponseNewVirtualKeysMapper;
import it.pagopa.pn.bff.mappers.virtualkeys.VirtualKeysMapper;
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
public class VirtualKeysPgService {

    private final PnVirtualKeysManagerClientPGImpl pnVirtualKeysManagerClientPG;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get a paginated list of the virtual keys that belong to a Customer/Recipient and are accessible by the current user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Customer/Recipient Type
     * @param xPagopaPnCxId     PCustomer/Recipient id
     * @param xPagopaPnCxRole   Customer/Recipient role
     * @param xPagopaPnCxGroups Customer/Recipient Group id List
     * @param limit             Number of items per page
     * @param lastKey           The last key returned by the previous search. If null, it will be returned the keys of the first page
     * @param lastUpdate        The update date of the last key returned by the previous search. If null, it will be returned the keys of the first page
     * @param showVirtualKey    Flag to show/hide the virtual key
     * @return the list of the virtual keys or error
     */
    public Mono<BffVirtualKeysResponse> getVirtualKeys(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                       String xPagopaPnCxId, String xPagopaPnCxRole, List<String> xPagopaPnCxGroups,
                                                       Integer limit, String lastKey,
                                                       String lastUpdate, Boolean showVirtualKey
    ) {
        log.info("Get virtual key list - senderId: {} - type: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

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
        return virtualKeysResponse.map(VirtualKeysMapper.modelMapper::mapVirtualKeysResponse);
    }

    /**
     * Delete a virtual key
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Customer/Recipient Type
     * @param xPagopaPnCxRole   Customer/Recipient Role
     * @param xPagopaPnCxId     Customer/Recipient id
     * @param kid               Virtual Key kid
     * @param xPagopaPnCxGroups Customer/Recipient Group id List
     * @return void
     */
    public Mono<Void> deleteVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                       String xPagopaPnCxId, String kid,
                                       List<String> xPagopaPnCxGroups, String xPagopaPnCxRole
    ) {
        log.info("Delete virtual key - senderId: {} - type: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        return pnVirtualKeysManagerClientPG.deleteVirtualKey(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertVirtualKeysPGCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                kid,
                xPagopaPnCxGroups,
                xPagopaPnCxRole
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Create new virtual key
     *
     * @param xPagopaPnUid         User Identifier
     * @param xPagopaPnCxType      Customer/Recipient Type
     * @param xPagopaPnCxId        Customer/Recipient id
     * @param xPagopaPnCxRole      Customer/Recipient role
     * @param requestNewVirtualKey Request that contains the name and the groups of the new virtual key
     * @param xPagopaPnCxGroups    Customer/Recipient Group id List
     * @return the id and the value of the new virtual key
     */
    public Mono<BffNewVirtualKeyResponse> newVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                        String xPagopaPnCxId, Mono<BffNewVirtualKeyRequest> requestNewVirtualKey,
                                                        List<String> xPagopaPnCxGroups, String xPagopaPnCxRole) {
        log.info("Create new virtual key - senderId: {} - type: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        return requestNewVirtualKey.flatMap(request -> {
            Mono<ResponseNewVirtualKey> responseNewVirtualKey = pnVirtualKeysManagerClientPG.newVirtualKey(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertVirtualKeysPGCXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    RequestNewVirtualKeysMapper.modelMapper.mapRequestNewVirtualKey(request),
                    xPagopaPnCxGroups,
                    xPagopaPnCxRole
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

            return responseNewVirtualKey.map(ResponseNewVirtualKeysMapper.modelMapper::mapResponseNewVirtualKey);
        });
    }

    /**
     * Change the status of a virtual key
     *
     * @param xPagopaPnUid               User Identifier
     * @param xPagopaPnCxType            Customer/Recipient Type
     * @param xPagopaPnCxId              Customer/Recipient id
     * @param xPagopaPnCxRole            Customer/Recipient role
     * @param kid                        kid of the virtual key to change status
     * @param bffVirtualKeyStatusRequest The new virtual key status
     * @param xPagopaPnCxGroups          Customer/Recipient Group id List
     * @return void
     */
    public Mono<Void> changeStatusVirtualKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                             String xPagopaPnCxId, String kid, Mono<BffVirtualKeyStatusRequest> bffVirtualKeyStatusRequest,
                                             List<String> xPagopaPnCxGroups, String xPagopaPnCxRole) {
        log.info("Change virtual key {} status - senderId: {} - type: {} - groups: {}", kid, xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);
        return bffVirtualKeyStatusRequest.flatMap(request ->
                pnVirtualKeysManagerClientPG.changeStatusVirtualKey(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertVirtualKeysPGCXType(xPagopaPnCxType),
                        xPagopaPnCxId,
                        kid,
                        RequestVirtualKeyStatusMapper.modelMapper.mapRequestVirtualKeyStatus(request),
                        xPagopaPnCxGroups,
                        xPagopaPnCxRole
                ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
        );
    }
}