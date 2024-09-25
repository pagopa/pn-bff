package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeyResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeysResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.*;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.publickeys.*;
import it.pagopa.pn.bff.pnclient.apikeys.PnPublicKeyManagerClientPGImpl;
import it.pagopa.pn.bff.pnclient.userattributes.PnUserAttributesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static reactor.core.publisher.Mono.zip;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicKeysPgService {
    private final PnPublicKeyManagerClientPGImpl pnPublickeyManagerClientPG;
    private final PnBffExceptionUtility pnBffExceptionUtility;
    private final PnUserAttributesClientImpl pnUserAttributesClient;

    /**
     * Get a paginated list of the public keys that belong to a PG and are accessible by the current user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   PG Type
     * @param xPagopaPnCxId     PG id
     * @param xPagopaPnCxRole   PG user Role
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
        log.info("Get public keys list - senderId: {} - type: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

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

        return publicKeysResponse.map(PublicKeysResponseMapper.modelMapper::mapPublicKeysResponse);
    }

    /**
     * Create new public key
     *
     * @param xPagopaPnUid        User Identifier
     * @param xPagopaPnCxType     PG Type
     * @param xPagopaPnCxId       PG id
     * @param xPagopaPnCxRole     PG user Role
     * @param bffPublicKeyRequest Request that contains the name and the groups of the new public key
     * @param xPagopaPnCxGroups   PG Group id List
     * @return BffPublicKeyResponse
     */
    public Mono<BffPublicKeyResponse> newPublicKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                   String xPagopaPnCxId, String xPagopaPnCxRole,
                                                   Mono<BffPublicKeyRequest> bffPublicKeyRequest, List<String> xPagopaPnCxGroups) {
        log.info("Create new public key - senderId: {} - type: {} - groups: {}", xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        return bffPublicKeyRequest.flatMap(request -> {
            Mono<PublicKeyResponse> publicKeyResponse = pnPublickeyManagerClientPG.newPublicKey(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertPublicKeysPGCXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    xPagopaPnCxRole,
                    PublicKeyRequestMapper.modelMapper.mapPublicKeyRequest(request),
                    xPagopaPnCxGroups
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

            return publicKeyResponse.map(PublicKeyResponseMapper.modelMapper::mapPublicKeyResponse);
        });

    }

    /**
     * Delete a public key
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Customer/Receiver Type
     * @param xPagopaPnCxId     Customer/Receiver Identifier
     * @param xPagopaPnCxRole   User role
     * @param id                unique identifier for the public key to be deleted
     * @param xPagopaPnCxGroups PG Group id List
     * @return
     */
    public Mono<Void> deletePublicKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                      String xPagopaPnCxId, String xPagopaPnCxRole,
                                      String id, List<String> xPagopaPnCxGroups) {
        log.info("Delete public key {} - senderId: {} - type: {} - groups: {}", id, xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        return pnPublickeyManagerClientPG.deletePublicKey(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertPublicKeysPGCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                xPagopaPnCxRole,
                id,
                xPagopaPnCxGroups

        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Change the status of a public key
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Customer/Receiver Type
     * @param xPagopaPnCxId     Customer/Receiver Identifier
     * @param xPagopaPnCxRole   User role
     * @param id                unique identifier for the public key to be changed
     * @param status            Action to change public key's status
     * @param xPagopaPnCxGroups Customer Groups
     * @return
     */
    public Mono<Void> changeStatusPublicKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                            String xPagopaPnCxId, String xPagopaPnCxRole,
                                            String id, String status,
                                            List<String> xPagopaPnCxGroups) {
        log.info("Change public key {} status - senderId: {} - type: {} - groups: {}", id, xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        return pnPublickeyManagerClientPG.changeStatusPublicKey(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertPublicKeysPGCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                xPagopaPnCxRole,
                id,
                status,
                xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }

    /**
     * Rotate the public key identified by its id
     *
     * @param xPagopaPnUid        User Identifier
     * @param xPagopaPnCxType     Customer/Receiver Type
     * @param xPagopaPnCxId       Customer/Receiver Identifier
     * @param xPagopaPnCxRole     User role
     * @param id                  Unique identifier for the public key to be rotated
     * @param bffPublicKeyRequest The publicKeyRequest parameter
     * @param xPagopaPnCxGroups   Customer Groups
     * @return BffPublicKeyResponse
     */
    public Mono<BffPublicKeyResponse> rotatePublicKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                      String xPagopaPnCxId, String xPagopaPnCxRole,
                                                      String id, Mono<BffPublicKeyRequest> bffPublicKeyRequest,
                                                      List<String> xPagopaPnCxGroups) {
        log.info("Rotate public key {} status - senderId: {} - type: {} - groups: {}", id, xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups);

        return bffPublicKeyRequest.flatMap(request -> {
            Mono<PublicKeyResponse> publicKeyResponse = pnPublickeyManagerClientPG.rotatePublicKey(
                    xPagopaPnUid,
                    CxTypeMapper.cxTypeMapper.convertPublicKeysPGCXType(xPagopaPnCxType),
                    xPagopaPnCxId,
                    xPagopaPnCxRole,
                    id,
                    PublicKeyRequestMapper.modelMapper.mapPublicKeyRequest(request),
                    xPagopaPnCxGroups
            ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

            return publicKeyResponse.map(PublicKeyResponseMapper.modelMapper::mapPublicKeyResponse);
        });
    }

    /**
     * Get the issuer status
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   PG Type
     * @param xPagopaPnCxId     PG id
     * @return BffPublicKeysIssuerResponse
     */
    public Mono<BffPublicKeysCheckIssuerResponse> checkIssuerPublicKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                       String xPagopaPnCxId) {

        log.info("Public keys check issuer - senderId: {} - type: {}", xPagopaPnCxId, xPagopaPnCxType);

        Mono<PublicKeysIssuerResponse> publicKeysIssuerResponse = pnPublickeyManagerClientPG.getIssuerStatus(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertPublicKeysPGCXType(xPagopaPnCxType),
                xPagopaPnCxId
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
        .map(PublicKeysIssuerStatusMapper.modelMapper::mapPublicKeysIssuerStatus);

        // call api to verify tos acceptance
        Mono<Consent> pnUserAttributesResp = pnUserAttributesClient.getConsentByType(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertUserAttributesPGCXType(xPagopaPnCxType),
                ConsentType.TOS_DEST_B2B
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return Mono.zip(publicKeysIssuerResponse, pnUserAttributesResp).map(res -> PublicKeysCheckIssuerStatusMapper.modelMapper.mapPublicKeysIssuerStatus(res.getT1(), res.getT2()));
    }
}