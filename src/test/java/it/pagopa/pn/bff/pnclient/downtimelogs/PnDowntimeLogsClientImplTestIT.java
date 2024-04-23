package it.pagopa.pn.bff.pnclient.downtimelogs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.api.DowntimeApi;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
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
    @Autowired
    private PnDowntimeLogsClientImpl pnDowntimeLogsClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.api.DowntimeApi")
    private DowntimeApi downtimeApi;

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
        mockServer.when(request().withMethod("GET").withPath(path + "/status"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDowntimeLogsClient.getCurrentStatus())
                .expectError(PnBffException.class).verify();
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
        mockServer.when(request().withMethod("GET").withPath(path + "/history"))
                .respond(response().withStatusCode(404));

        StepVerifier.create(pnDowntimeLogsClient.getStatusHistory(
                        OffsetDateTime.parse("1900-01-01T00:00:00Z"),
                        OffsetDateTime.now(ZoneOffset.UTC),
                        downtimeLogsMock.getFunctionalityMock(),
                        "0",
                        "10"
                ))
                .expectError(PnBffException.class).verify();
    }
}