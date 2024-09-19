package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.UserConsentsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.TosPrivacyService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@CustomLog
@RestController
public class TosPrivacyController implements UserConsentsApi {
    private final TosPrivacyService tosPrivacyService;

    public TosPrivacyController(TosPrivacyService tosPrivacyService) {
        this.tosPrivacyService = tosPrivacyService;
    }

    /**
     * GET /bff/v1/tos-privacy : Tos & Privacy information
     * Get the Tos & Privacy information of the user
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Public Administration Type
     * @param exchange
     * @return the Tos & Privacy information of the user
     */
    @Override
    public Mono<ResponseEntity<BffTosPrivacyConsent>> getTosPrivacyV1(String xPagopaPnUid,
                                                                      CxTypeAuthFleet xPagopaPnCxType,
                                                                      final ServerWebExchange exchange) {

        Mono<BffTosPrivacyConsent> serviceResponse = tosPrivacyService
                .getTosPrivacy(xPagopaPnUid, xPagopaPnCxType);

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * PUT /bff/v1/tos-privacy : Acceptance of TOS and Privacy
     * Allows to accept the TOS and Privacy.
     *
     * @param xPagopaPnUid    User Identifier
     * @param xPagopaPnCxType Public Administration Type
     * @param tosPrivacyBody  Body of the request containing the acceptance of the TOS and Privacy
     * @param exchange
     * @return
     */
    @Override
    public Mono<ResponseEntity<Void>> acceptTosPrivacyV1(String xPagopaPnUid,
                                                         CxTypeAuthFleet xPagopaPnCxType,
                                                         Mono<BffTosPrivacyBody> tosPrivacyBody,
                                                         ServerWebExchange exchange) {

        Mono<Void> serviceResponse = tosPrivacyService
                .acceptOrDeclineTosPrivacy(xPagopaPnUid, xPagopaPnCxType, tosPrivacyBody);

        return serviceResponse
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }
}