package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeResponse;
import it.pagopa.pn.bff.mocks.AuthFleetMock;
import it.pagopa.pn.bff.pnclient.auth.PnAuthFleetClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {AuthService.class})
public class AuthServiceTest {
    @Autowired
    private AuthService authService;
    private PnAuthFleetClientImpl pnAuthFleetClient;
    private final AuthFleetMock authFleetMock = new AuthFleetMock();

    @BeforeEach
    void setup() {
        this.pnAuthFleetClient = mock(PnAuthFleetClientImpl.class);

        this.authService = new AuthService(pnAuthFleetClient);
    }

    @Test
    void testPostTokenExchangeOk() {
        TokenExchangeResponse tokenExchangeResponse = authFleetMock.getTokenExchangeResponse();

        when(pnAuthFleetClient.postTokenExchange(
                Mockito.anyString(),
                Mockito.any(TokenExchangeBody.class)
        )).thenReturn(Mono.just(tokenExchangeResponse));

        StepVerifier.create(authService.tokenExchange(
                        authFleetMock.ORIGIN,
                        Mono.just(authFleetMock.getTokenExchangeBody())
                ))
                .expectNext(authFleetMock.getTokenExchangeResponse())
                .verifyComplete();
    }

    @Test
    void testPostTokenExchangeError() {
        when(pnAuthFleetClient.postTokenExchange(
                Mockito.anyString(),
                Mockito.any(TokenExchangeBody.class)
        )).thenReturn(Mono.error(new WebClientResponseException(400, "Bad Request", null, null, null)));

        StepVerifier.create(authService.tokenExchange(
                        authFleetMock.ORIGIN,
                        Mono.just(authFleetMock.getTokenExchangeBody())
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400)
                .verify();
    }
}
