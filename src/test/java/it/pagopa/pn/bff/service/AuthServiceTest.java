package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTokenExchangeResponse;
import it.pagopa.pn.bff.mocks.AuthFleetMock;
import it.pagopa.pn.bff.pnclient.auth.PnAuthFleetClientImpl;
import org.junit.jupiter.api.BeforeAll;
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
    private static AuthService authService;
    private static PnAuthFleetClientImpl pnAuthFleetClient;
    private final AuthFleetMock authFleetMock = new AuthFleetMock();

    @BeforeAll
    public static void setup() {
        pnAuthFleetClient = mock(PnAuthFleetClientImpl.class);

        authService = new AuthService(pnAuthFleetClient);
    }

    @Test
    void tokenExchange() {
        BffTokenExchangeResponse tokenExchangeResponse = authFleetMock.getTokenExchangeResponse();

        when(pnAuthFleetClient.postTokenExchange(
                Mockito.anyString(),
                Mockito.any(BffTokenExchangeBody.class)
        )).thenReturn(Mono.just(tokenExchangeResponse));

        StepVerifier.create(authService.tokenExchange(
                        AuthFleetMock.ORIGIN,
                        Mono.just(authFleetMock.getTokenExchangeBody())
                ))
                .expectNext(authFleetMock.getTokenExchangeResponse())
                .verifyComplete();
    }

    @Test
    void tokenExchangeError() {
        when(pnAuthFleetClient.postTokenExchange(
                Mockito.anyString(),
                Mockito.any(BffTokenExchangeBody.class)
        )).thenReturn(Mono.error(new WebClientResponseException(400, "Bad Request", null, null, null)));

        StepVerifier.create(authService.tokenExchange(
                        AuthFleetMock.ORIGIN,
                        Mono.just(authFleetMock.getTokenExchangeBody())
                ))
                .expectErrorMatches(throwable -> throwable instanceof PnBffException
                        && ((PnBffException) throwable).getProblem().getStatus() == 400)
                .verify();
    }
}