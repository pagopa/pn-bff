package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeResponse;
import it.pagopa.pn.bff.pnclient.auth.PnAuthFleetClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final PnAuthFleetClientImpl pnAuthFleetClient;

    public Mono<TokenExchangeResponse> tokenExchange(
            String origin,
            Mono<TokenExchangeBody> tokenExchangeBody
    ) {
        log.info("tokenExchange");

        return tokenExchangeBody
                .flatMap(body -> pnAuthFleetClient.postTokenExchange(origin, body))
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}
