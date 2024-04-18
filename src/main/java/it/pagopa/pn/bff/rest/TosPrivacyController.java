package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.UserConsentsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.TosPrivacyService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
        log.logStartingProcess("getTosPrivacyV1");

        Mono<BffTosPrivacyConsent> serviceResponse;
        serviceResponse = tosPrivacyService
                .getTosPrivacy(xPagopaPnUid, xPagopaPnCxType)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        log.logEndingProcess("getTosPrivacyV1");

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
    public Mono<ResponseEntity<Void>> acceptTosPrivacy(String xPagopaPnUid,
                                                       CxTypeAuthFleet xPagopaPnCxType,
                                                       Mono<BffTosPrivacyBody> tosPrivacyBody,
                                                       ServerWebExchange exchange) {
        log.logStartingProcess("putTosPrivacyV1");

        Mono<Void> serviceResponse;
        serviceResponse = tosPrivacyService.acceptOrDeclineTosPrivacy(xPagopaPnUid, xPagopaPnCxType, tosPrivacyBody);

        log.logEndingProcess("putTosPrivacyV1");

        return serviceResponse.map(response -> ResponseEntity.noContent().build());
    }
}