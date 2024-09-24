package it.pagopa.pn.bff.pnclient.apikeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.api.PublicKeysApi;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.*;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnPublicKeyManagerClientPGImpl {

    private final PublicKeysApi publicKeysApi;

    public Mono<PublicKeysResponse> getPublicKeys(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                  String xPagopaPnCxId, String xPagopaPnCxRole,
                                                  List<String> xPagopaPnCxGroups, Integer limit, String lastKey,
                                                  String createdAt, Boolean showPublicKey) {

//        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_PUBLICKEY_MANAGER, "getPublicKeys");

        return publicKeysApi.getPublicKeys(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                xPagopaPnCxGroups,
                limit,
                lastKey,
                createdAt,
                showPublicKey
        );
    }

    public Mono<PublicKeyResponse> newPublicKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                String xPagopaPnCxId, String xPagopaPnCxRole,
                                                PublicKeyRequest publicKeyRequest, List<String> xPagopaPnCxGroups) {

//        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_PUBLICKEY_MANAGER, "newPublicKey");

        return publicKeysApi.newPublicKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                publicKeyRequest,
                xPagopaPnCxGroups
        );
    }

    public Mono<Void> deletePublicKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                      String xPagopaPnCxId, String xPagopaPnCxRole,
                                      String id, List<String> xPagopaPnCxGroups) {
//        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_PUBLICKEY_MANAGER, "deletePublicKeys");

        return publicKeysApi.deletePublicKeys(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                id,
                xPagopaPnCxGroups
        );
    }

    public Mono<Void> changeStatusPublicKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                            String xPagopaPnCxId, String xPagopaPnCxRole,
                                            String id, String status, List<String> xPagopaPnCxGroups) {
//        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_PUBLICKEY_MANAGER, "deletePublicKey");

        return publicKeysApi.changeStatusPublicKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                id,
                status,
                xPagopaPnCxGroups
        );
    }

    public Mono<PublicKeyResponse> rotatePublicKey(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                   String xPagopaPnCxId, String xPagopaPnCxRole,
                                                   String id, PublicKeyRequest publicKeyRequest,
                                                   List<String> xPagopaPnCxGroups) {
        return publicKeysApi.rotatePublicKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                id,
                publicKeyRequest,
                xPagopaPnCxGroups
        );
    }

    public Mono<PublicKeysIssuerResponse> getIssuerStatus(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                          String xPagoPaPnCxId) {
        return publicKeysApi.getIssuerStatus(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagoPaPnCxId
        );
    }
}
