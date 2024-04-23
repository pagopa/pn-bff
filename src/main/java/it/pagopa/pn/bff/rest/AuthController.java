package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.TokenExchangeApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeResponse;
import it.pagopa.pn.bff.service.AuthService;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@CustomLog
@RestController
public class AuthController implements TokenExchangeApi {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public Mono<ResponseEntity<TokenExchangeResponse>> tokenExchangeV1(
            Mono<TokenExchangeBody> tokenExchangeBodyMono,
            final ServerWebExchange exchange
    ) {
        log.logStartingProcess("tokenExchangeV1");
        Mono<ResponseEntity<TokenExchangeResponse>> response = tokenExchangeBodyMono
                .flatMap(authService::tokenExchange)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        log.logEndingProcess("tokenExchangeV1");
        return response;
    }
}
