package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.UserConsentsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.TosPrivacyService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class TosPrivacyController implements UserConsentsApi {
    private final TosPrivacyService tosPrivacyService;

    public TosPrivacyController(TosPrivacyService tosPrivacyService) {
        this.tosPrivacyService = tosPrivacyService;
    }

    /**
     * PUT /bff/v1/pg/tos-privacy : Acceptance of Pg TOS and Privacy
     * Allows to accept the Pg TOS and Privacy.
     *
     * @param xPagopaPnCxId           User Identifier
     * @param xPagopaPnCxType         Customer/Recipient Type
     * @param bffTosPrivacyActionBody Body of the request containing the acceptance of the Pg TOS and Privacy
     * @param xPagopaPnCxRole         Customer/Recipient Role
     * @param xPagopaPnCxGroups       List of Customer/Recipient groups
     * @param exchange
     * @return
     */
    @Override
    public Mono<ResponseEntity<Void>> acceptPgTosPrivacyV1(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType,
                                                           Flux<BffTosPrivacyActionBody> bffTosPrivacyActionBody,
                                                           String xPagopaPnCxRole, List<String> xPagopaPnCxGroups, final ServerWebExchange exchange) {

        Mono<Void> serviceResponse = tosPrivacyService
                .acceptOrDeclinePgTosPrivacy(xPagopaPnCxId, xPagopaPnCxType, bffTosPrivacyActionBody, xPagopaPnCxRole, xPagopaPnCxGroups);

        return serviceResponse
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }


    /**
     * GET /bff/v1/pg/tos-privacy : Pg Tos & Privacy information
     * Get the Pg Tos & Privacy information of the user
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Customer/Recipient Type
     * @param type            Consent Type
     * @param exchange
     * @return the Pg Tos & Privacy information of the user
     */
    @Override
    public Mono<ResponseEntity<Flux<BffConsent>>> getPgConsentByType(String xPagopaPnUid,
                                                                     CxTypeAuthFleet xPagopaPnCxType,
                                                                     List<ConsentType> type,
                                                                     final ServerWebExchange exchange) {

        Flux<BffConsent> serviceResponse = tosPrivacyService
                .getPgTosPrivacy(xPagopaPnUid, xPagopaPnCxType, type);

        return serviceResponse.collectList()
                .map(consents -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(consents)));
    }

    /**
     * GET /bff/v2/tos-privacy : Tos & Privacy information
     * Get the Tos & Privacy information of the user
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Public Administration Type
     * @param type            List of consents to retrieve
     * @param exchange
     * @return the Tos & Privacy information of the user
     */
    @Override
    public Mono<ResponseEntity<Flux<BffConsent>>> getTosPrivacyV2(String xPagopaPnUid,
                                                                  CxTypeAuthFleet xPagopaPnCxType,
                                                                  List<ConsentType> type,
                                                                  final ServerWebExchange exchange) {

        Flux<BffConsent> serviceResponse = tosPrivacyService
                .getTosPrivacy(xPagopaPnUid, xPagopaPnCxType, type);

        return serviceResponse.collectList()
                .map(consents -> ResponseEntity.status(HttpStatus.OK).body(Flux.fromIterable(consents)));
    }

    /**
     * PUT /bff/v2/tos-privacy : Acceptance of TOS and Privacy
     * Allows to accept the TOS and Privacy.
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Public Administration Type
     * @param tosPrivacyBody  Body of the request containing the acceptance of the TOS and Privacy
     * @param exchange
     * @return void
     */
    @Override
    public Mono<ResponseEntity<Void>> acceptTosPrivacyV2(String xPagopaPnUid,
                                                         CxTypeAuthFleet xPagopaPnCxType,
                                                         Flux<BffTosPrivacyActionBody> tosPrivacyBody,
                                                         ServerWebExchange exchange) {

        Mono<Void> serviceResponse = tosPrivacyService
                .acceptOrDeclineTosPrivacy(xPagopaPnUid, xPagopaPnCxType, tosPrivacyBody);

        return serviceResponse
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }
}