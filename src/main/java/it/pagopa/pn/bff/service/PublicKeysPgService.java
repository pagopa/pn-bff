package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroup;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPublicKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPublicKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.PublicKeyRequest;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.publickeys.PublicKeysResponseMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.pnclient.publickeys.PnPublicKeyManagerClientPGImpl;
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
public class PublicKeysPgService {
    private final PnPublicKeyManagerClientPGImpl pnPublickeyManagerClientPG;
    private final PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get a paginated list of the public keys that belong to a PG and are accessible by the current user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   PG Type
     * @param xPagopaPnCxId     PG id
     * @param xPagopaPnCxGroups PG Group id List
     * @param limit             Number of items per page
     * @param lastKey           The last key returned by the previous search. If null, it will be returned the keys of the first page
     * @param createdAt         The update date of the last key returned by the previous search. If null, it will be returned the keys of the first page
     * @param showPublicKey     Flag to show/hide the public key
     * @return the list of the public keys or error
     */
    public Mono<BffPublicKeysResponse> getPublicKeys(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                     String xPagopaPnCxId, String xPagopaPnCxRole,
                                                     List<String> xPagopaPnCxGroups, Integer limit, String lastKey,
                                                     String createdAt, Boolean showPublicKey
    ) {
        // list of public keys
        log.info("Get public key list - senderId: {} - type: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        Mono<PublicKeysResponse> publicKeysResponse = pnPublickeyManagerClientPG.getPublicKeys(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertPublicKeysPGCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                xPagopaPnCxRole,
                xPagopaPnCxGroups,
                limit,
                lastKey,
                createdAt,
                showPublicKey
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        // list of groups linked to pg
        log.info("Get user groups - senderId: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxGroups);

        Mono<List<PgGroup>> pgGroups = pnExternalRegistriesClient.getPgGroups(
                xPagopaPnUid,
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                null
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException).collectList();

        return Mono.zip(publicKeysResponse, pgGroups).map(res -> PublicKeysResponseMapper.modelMapper.mapPublicKeysResponse(res.getT1(), res.getT2()));
    }

    /**
     * Create new public key
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   PG Type
     * @param xPagopaPnCxId     PG id
     * @param publicKeyRequest  Request that contains the name and the groups of the new public key
     * @param xPagopaPnCxGroups PG Group id List
     * @return the id and the value of the public key
     */
//    public Mono<BffPublicKeyResponse> newPublicKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
//                                                   String xPagopaPnCxId, String xPagopaPnCxRole,
//                                                   PublicKeyRequest publicKeyRequest, List<String> xPagopaPnCxGroups){
//        log.info("Create new api key - senderId: {} - type: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);
//
//        return
//        })
//
//    }
}
