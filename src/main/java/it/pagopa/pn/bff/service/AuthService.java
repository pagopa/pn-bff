package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTokenExchangeResponse;
import it.pagopa.pn.bff.pnclient.auth.PnAuthFleetClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
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
    private final PnBffExceptionUtility pnBffExceptionUtility;

    public Mono<BffTokenExchangeResponse> tokenExchange(
            String origin,
            Mono<BffTokenExchangeBody> tokenExchangeBody
    ) {
        log.info("tokenExchange");

        return tokenExchangeBody
                .flatMap(body -> pnAuthFleetClient.postTokenExchange(origin, body))
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);
    }
}