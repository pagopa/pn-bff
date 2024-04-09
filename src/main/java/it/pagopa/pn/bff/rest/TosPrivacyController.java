package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.UserConsentsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyConsent;
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

    @Override
    public Mono<ResponseEntity<TosPrivacyConsent>> getTosPrivacyV1(String xPagopaPnUid,
                                                                   CxTypeAuthFleet xPagopaPnCxType,
                                                                   final ServerWebExchange exchange) {
        log.logStartingProcess("getTosPrivacyV1");

        Mono<TosPrivacyConsent> serviceResponse;
        serviceResponse = tosPrivacyService
                .getTosPrivacy(xPagopaPnUid, xPagopaPnCxType)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        log.logEndingProcess("getTosPrivacyV1");

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    @Override
    public Mono<ResponseEntity<Void>> putTosPrivacyV1(String xPagopaPnUid,
                                                      CxTypeAuthFleet xPagopaPnCxType,
                                                      Mono<TosPrivacyBody> tosPrivacyBody,
                                                      ServerWebExchange exchange) {
        log.logStartingProcess("putTosPrivacyV1");

        Mono<Void> serviceResponse;
        serviceResponse = tosPrivacyService.acceptOrDeclineTosPrivacy(xPagopaPnUid, xPagopaPnCxType, tosPrivacyBody);

        log.logEndingProcess("putTosPrivacyV1");

        return serviceResponse.map(response -> ResponseEntity.noContent().build());
    }
}