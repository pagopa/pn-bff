package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.TokenExchangeApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTokenExchangeResponse;
import it.pagopa.pn.bff.service.AuthService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@CustomLog
@RestController
public class AuthController implements TokenExchangeApi {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Mono<ResponseEntity<BffTokenExchangeResponse>> tokenExchangeV1(
            Mono<BffTokenExchangeBody> tokenExchangeBodyMono,
            final ServerWebExchange exchange
    ) {

        Mono<BffTokenExchangeResponse> serviceResponse = authService.tokenExchange(
                exchange.getRequest().getHeaders().getOrigin(),
                tokenExchangeBodyMono
        );

        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}