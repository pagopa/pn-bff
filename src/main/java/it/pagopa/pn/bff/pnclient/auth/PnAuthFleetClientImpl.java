package it.pagopa.pn.bff.pnclient.auth;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeResponse;
import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import lombok.CustomLog;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Component
@CustomLog
public class PnAuthFleetClientImpl extends CommonBaseClient {
    private WebClient webClient;
    private final String pnAuthFleetBaseUrl;

    public PnAuthFleetClientImpl(PnBffConfigs config) {
        this.pnAuthFleetBaseUrl = config.getAuthFleetBaseUrl();
    }

    @PostConstruct
    public void init() {
        this.webClient = super.enrichBuilder(WebClient.builder().baseUrl(pnAuthFleetBaseUrl)).build();
    }

    public Mono<TokenExchangeResponse> postTokenExchange(String origin, TokenExchangeBody body) {
        log.info("Invoking pn-auth-fleet token-exchange");

        return this.webClient.post()
                .uri("/token-exchange")
                .header("Origin", origin)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TokenExchangeResponse.class)
                .doOnSuccess(response -> log.info("Response from pn-auth-fleet: {}", response))
                .doOnError(throwable -> log.error("Error in pn-auth-fleet token-exchange", throwable));
    }


}
