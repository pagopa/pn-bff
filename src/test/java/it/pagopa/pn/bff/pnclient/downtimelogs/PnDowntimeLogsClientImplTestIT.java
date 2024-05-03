package it.pagopa.pn.bff.pnclient.downtimelogs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.mocks.DowntimeLogsMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PnDowntimeLogsClientImplTestIT {
    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;
    private final DowntimeLogsMock downtimeLogsMock = new DowntimeLogsMock();
    private final String path = "/downtime/v1";
    private final String LEGAL_FACT_ID = "LEGAL_FACT_ID";
    @Autowired
    private PnDowntimeLogsClientImpl pnDowntimeLogsClient;

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
    void getCurrentStatus() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(downtimeLogsMock.getStatusMockOK());
        mockServerClient.when(request().withMethod("GET").withPath(path + "/status"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDowntimeLogsClient.getCurrentStatus())
                .expectNext(downtimeLogsMock.getStatusMockOK()).verifyComplete();
    }

    @Test
    void getCurrentStatusError() {
        mockServerClient.when(request().withMethod("GET").withPath(path + "/status"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDowntimeLogsClient.getCurrentStatus())
                .expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getStatusHistory() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        String response = objectMapper.writeValueAsString(downtimeLogsMock.getDowntimeHistoryMock());
        mockServerClient.when(request().withMethod("GET").withPath(path + "/history"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDowntimeLogsClient.getStatusHistory(
                        OffsetDateTime.parse("1900-01-01T00:00:00Z"),
                        OffsetDateTime.now(ZoneOffset.UTC),
                        downtimeLogsMock.getFunctionalityMock(),
                        "0",
                        "10"
                ))
                .expectNext(downtimeLogsMock.getDowntimeHistoryMock()).verifyComplete();
    }

    @Test
    void getStatusHistoryError() {
        mockServerClient.when(request().withMethod("GET").withPath(path + "/history"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDowntimeLogsClient.getStatusHistory(
                        OffsetDateTime.parse("1900-01-01T00:00:00Z"),
                        OffsetDateTime.now(ZoneOffset.UTC),
                        downtimeLogsMock.getFunctionalityMock(),
                        "0",
                        "10"
                ))
                .expectError(WebClientResponseException.class).verify();
    }

    @Test
    void getLegalFact() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(downtimeLogsMock.getLegalFactMetadataMock());
        mockServerClient.when(request().withMethod("GET").withPath(path + "/legal-facts/" + LEGAL_FACT_ID))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(response)
                );

        StepVerifier.create(pnDowntimeLogsClient.getLegalFact(
                        LEGAL_FACT_ID
                ))
                .expectNext(downtimeLogsMock.getLegalFactMetadataMock()).verifyComplete();
    }

    @Test
    void getLegalFactError() {
        mockServerClient.when(request().withMethod("GET").withPath(path + "/legal-facts/" + LEGAL_FACT_ID))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDowntimeLogsClient.getLegalFact(
                        LEGAL_FACT_ID
                ))
                .expectError(WebClientResponseException.class).verify();
    }
}