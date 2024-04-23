package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthService {
    private final WebClient webClient;

    public AuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://webapi.dev.notifichedigitali.it").build();
    }

    public Mono<ResponseEntity<TokenExchangeResponse>> tokenExchange(TokenExchangeBody tokenExchangeBody
    ) {
        log.info("tokenExchange");
        return this.webClient.post()
                .uri("/token-exchange")
                .header("Origin", "https://cittadini.dev.notifichedigitali.it")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(tokenExchangeBody)
                .retrieve()
                .bodyToMono(TokenExchangeResponse.class)
                .map(ResponseEntity::ok)
                .onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}
