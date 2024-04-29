package it.pagopa.pn.bff.pnclient.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.bff.mocks.AuthFleetMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PnAuthFleetClientImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final String path = "/token-exchange";
    private final AuthFleetMock authFleetMock = new AuthFleetMock();

    @Autowired
    PnAuthFleetClientImpl pnAuthFleetClient;

    @BeforeAll
    public static void startMockServer() {
        mockServer = startClientAndServer(9998);
        mockServerClient = new MockServerClient("localhost", 9998);
    }

    @AfterAll
    public static void stopMockServer() {
        mockServerClient.close();
        mockServer.stop();
    }

    @AfterEach
    public void resetMockServerClient() {
        mockServerClient.reset();
    }

    @Test
    void postTokenExchangeOk() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(authFleetMock.getTokenExchangeBody());
        String response = objectMapper.writeValueAsString(authFleetMock.getTokenExchangeResponse());

        mockServerClient.when(request()
                .withMethod("POST")
                .withPath(path)
                .withHeader(HttpHeaders.ORIGIN, AuthFleetMock.ORIGIN)
                .withBody(request)
        ).respond(response()
                .withStatusCode(200)
                .withContentType(MediaType.APPLICATION_JSON)
                .withBody(response)
        );

        StepVerifier.create(pnAuthFleetClient.postTokenExchange(AuthFleetMock.ORIGIN, authFleetMock.getTokenExchangeBody()))
                .expectNext(authFleetMock.getTokenExchangeResponse())
                .verifyComplete();
    }

    @Test
    void postTokenExchangeError() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(authFleetMock.getTokenExchangeBody());

        mockServerClient.when(request()
                        .withMethod("POST")
                        .withPath(path)
                        .withHeader(HttpHeaders.ORIGIN, AuthFleetMock.ORIGIN)
                        .withBody(request)
                )
                .respond(response()
                        .withStatusCode(400)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody("error")
                );

        StepVerifier.create(pnAuthFleetClient.postTokenExchange(AuthFleetMock.ORIGIN, authFleetMock.getTokenExchangeBody()))
                .expectError()
                .verify();
    }
}
