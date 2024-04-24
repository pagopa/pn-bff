package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeBody;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TokenExchangeResponse;
import it.pagopa.pn.bff.mocks.AuthFleetMock;
import it.pagopa.pn.bff.service.AuthService;
import it.pagopa.pn.bff.utils.PnBffRestConstants;
import it.pagopa.pn.bff.utils.helpers.MonoHelpers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@ContextConfiguration(classes = AuthController.class)
@WebFluxTest
public class AuthControllerTest {
    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private AuthService authService;

    @SpyBean
    private AuthController authController;

    AuthFleetMock authFleetMock = new AuthFleetMock();


    @Test
    void postTokenExchange() {
        TokenExchangeBody request = new TokenExchangeBody();
        request.authorizationToken("authorizationToken");
        TokenExchangeResponse response = authFleetMock.getTokenExchangeResponse();

        Mockito.when(authService.tokenExchange(
                Mockito.any(),
                Mockito.any()
        )).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri(PnBffRestConstants.TOKEN_EXCHANGE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", authFleetMock.ORIGIN)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TokenExchangeResponse.class)
                .isEqualTo(response);

        Mockito.verify(authService).tokenExchange(
                eq(authFleetMock.ORIGIN),
                argThat((argumentToCompare -> MonoHelpers.monoComparator(argumentToCompare, Mono.just(request))))
        );
    }

    @Test
    void postTokenExchangeError() {
        TokenExchangeBody request = new TokenExchangeBody();
        request.authorizationToken("authorizationToken");

        Mockito.when(authService.tokenExchange(
                Mockito.any(),
                Mockito.any()
        )).thenReturn(Mono.error(new WebClientResponseException(500, "Error", null, null, null)));

        webTestClient.post()
                .uri(PnBffRestConstants.TOKEN_EXCHANGE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", authFleetMock.ORIGIN)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .is5xxServerError();

        Mockito.verify(authService).tokenExchange(
                eq(authFleetMock.ORIGIN),
                argThat((argumentToCompare -> MonoHelpers.monoComparator(argumentToCompare, Mono.just(request))))
        );
    }
}
