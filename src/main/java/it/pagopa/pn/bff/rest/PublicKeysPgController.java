package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.PublicKeysApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPublicKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPublicKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPublicKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.PublicKeysPgService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class PublicKeysPgController implements PublicKeysApi {
    private final PublicKeysPgService publicKeysPgService;

    public PublicKeysPgController(PublicKeysPgService publicKeysPgService) {
        this.publicKeysPgService = publicKeysPgService;
    }

    /**
     * GET /bff/v1/pg/public-keys : Public keys list
     * Get a paginated list of the public keys for the current PG user
     *
     * @param xPagopaPnUid      User Identifier (required)
     * @param xPagopaPnCxType   Customer/Receiver Type (required)
     * @param xPagopaPnCxId     Customer/Receiver Identifier (required)
     * @param xPagopaPnCxGroups Customer Groups (optional)
     * @param xPagopaPnCxRole   Ruolo (estratto da token di Self Care) (optional)
     * @param limit             (optional, default to 10)
     * @param lastKey           (optional)
     * @param createdAt         (optional)
     * @param showPublicKey     (optional, default to false)
     * @return the list of the public keys or error
     */
    @Override
    public Mono<ResponseEntity<BffPublicKeysResponse>> getPublicKeysV1(String xPagopaPnUid,
                                                                       CxTypeAuthFleet xPagopaPnCxType,
                                                                       String xPagopaPnCxId,
                                                                       List<String> xPagopaPnCxGroups,
                                                                       String xPagopaPnCxRole,
                                                                       Integer limit,
                                                                       String lastKey,
                                                                       String createdAt,
                                                                       Boolean showPublicKey,
                                                                       final ServerWebExchange exchange) {

        Mono<BffPublicKeysResponse> serviceResponse = publicKeysPgService.getPublicKeys(
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

        return serviceResponse.map((response -> ResponseEntity.status(HttpStatus.OK).body(response)));
    }

    /**
     * POST /bff/v1/pg/public-keys : Create a new public key
     *
     * @param xPagopaPnUid          User Identifier (required)
     * @param xPagopaPnCxType       Customer/Receiver Type (required)
     * @param xPagopaPnCxId         Customer/Receiver Identifier (required)
     * @param bffPublicKeyRequest   (required)
     * @param xPagopaPnCxGroups     Customer Groups (optional)
     * @param xPagopaPnCxRole       User's Role (derived by Self Care Token) (optional)
     * @return the public key just created
     */
    @Override
    public Mono<ResponseEntity<BffPublicKeyResponse>> newPublicKeyV1(String xPagopaPnUid,
                                                                     CxTypeAuthFleet xPagopaPnCxType,
                                                                     String xPagopaPnCxId,
                                                                     Mono<BffPublicKeyRequest> bffPublicKeyRequest,
                                                                     List<String> xPagopaPnCxGroups,
                                                                     String xPagopaPnCxRole,
                                                                     final ServerWebExchange exchange) {

        Mono<BffPublicKeyResponse> serviceResponse = publicKeysPgService.newPublicKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                bffPublicKeyRequest,
                xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * DELETE /bff/v1/pg/public-keys/{kid} : Delete the public key
     *
     * @param xPagopaPnUid      User Identifier (required)
     * @param xPagopaPnCxType   Customer/Receiver Type (required)
     * @param xPagopaPnCxId     Customer/Receiver Identifier (required)
     * @param kid               Public key unique identifier (required)
     * @param xPagopaPnCxGroups Customer Groups (optional)
     * @param xPagopaPnCxRole   User's Role (derived by Self Care Token) (optional)
     * @return
     */
    public Mono<ResponseEntity<Void>> deletePublicKeyV1(String xPagopaPnUid,
                                                        CxTypeAuthFleet xPagopaPnCxType,
                                                        String xPagopaPnCxId,
                                                        String kid,
                                                        List<String> xPagopaPnCxGroups,
                                                        String xPagopaPnCxRole,
                                                        final ServerWebExchange exchange) {
        Mono<Void> serviceResponse = publicKeysPgService.deletePublicKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                kid,
                xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * PUT /bff/v1/pg/public-key/{kid}/status : public key LOCK/UNLOCK
     * Lock/Unlock the public key identified by kid
     *
     * @param xPagopaPnUid      User Identifier (required)
     * @param xPagopaPnCxType   Customer/Receiver Type (required)
     * @param xPagopaPnCxId     Customer/Receiver Identifier (required)
     * @param kid               Public key unique identifier (required)
     * @param status            New Public Key status (required)
     * @param xPagopaPnCxGroups Customer Groups (optional)
     * @param xPagopaPnCxRole   User's Role (derived by Self Care Token) (optional)
     * @return
     */
    public Mono<ResponseEntity<Void>> changeStatusPublicKeyV1(String xPagopaPnUid,
                                                              CxTypeAuthFleet xPagopaPnCxType,
                                                              String xPagopaPnCxId,
                                                              String kid,
                                                              String status,
                                                              List<String> xPagopaPnCxGroups,
                                                              String xPagopaPnCxRole,
                                                              final ServerWebExchange exchange) {

        Mono<Void> serviceResposne = publicKeysPgService.changeStatusPublicKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                kid,
                status,
                xPagopaPnCxGroups
        );

        return serviceResposne.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * POST /bff/v1/pg/public-key/{kid}/rotate : Rotate public key
     * Rotate the Public Key identified by kid
     *
     * @param xPagopaPnUid          User Identifier (required)
     * @param xPagopaPnCxType       Customer/Receiver Type (required)
     * @param xPagopaPnCxId         Customer/Receiver Identifier (required)
     * @param kid                   Public key unique identifier (required)
     * @param bffPublicKeyRequest   (required)
     * @param xPagopaPnCxGroups     Customer Groups (optional)
     * @param xPagopaPnCxRole       User's Role (derived by Self Care Token) (optional)
     * @return the rotated public key
     */
    public Mono<ResponseEntity<BffPublicKeyResponse>> rotatePublicKeyV1(String xPagopaPnUid,
                                                                        CxTypeAuthFleet xPagopaPnCxType,
                                                                        String xPagopaPnCxId,
                                                                        String kid,
                                                                        Mono<BffPublicKeyRequest> bffPublicKeyRequest,
                                                                        List<String> xPagopaPnCxGroups,
                                                                        String xPagopaPnCxRole,
                                                                        final ServerWebExchange exchange) {

        Mono<BffPublicKeyResponse> serviceResponse = publicKeysPgService.rotatePublicKey(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnCxRole,
                kid,
                bffPublicKeyRequest,
                xPagopaPnCxGroups
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}

