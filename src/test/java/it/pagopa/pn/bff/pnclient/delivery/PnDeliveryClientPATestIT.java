package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@ContextConfiguration(classes = {PnDeliveryClientPAImpl.class})
@TestPropertySource(properties = {
        "pn.bff.delivery-base-url=http://localhost:9998",
})
public class PnDeliveryClientPATestIT {
    @Autowired
    private PnDeliveryClientPAImpl paDeliveryClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi")
    private SenderReadB2BApi senderReadB2BApi;
    private static ClientAndServer mockServer;

    final String xPagopaPnUid = "userId";
    final CxTypeAuthFleet xPagopaPnCxType = CxTypeAuthFleet.PA;
    final String xPagopaPnCxId = "cxId";
    final String iun = "DHUJ-QYVT-DMVH-202302-P-1";
    final List<String> xPagopaPnCxGroups = Collections.singletonList("group");
    final String path = "/delivery/v2.3/notifications/sent/" + iun;

    @BeforeAll
    public static void startMockServer() {
        mockServer = startClientAndServer(9998);
    }

    @AfterAll
    public static void stopMockServer() {
        mockServer.stop();
    }

    @Test
    void getSentNotification() {

        FullSentNotificationV23 mockNotification = new FullSentNotificationV23();

        when(senderReadB2BApi.getSentNotificationV23(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()))
                .thenReturn(Mono.just(mockNotification));

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath(path)
                )
                .respond(response()
                        .withStatusCode(200)
                );

        StepVerifier.create(paDeliveryClient.getSentNotification(
                        xPagopaPnUid,
                        xPagopaPnCxType,
                        xPagopaPnCxId,
                        iun,
                        xPagopaPnCxGroups
                ))
                .expectNext(mockNotification)
                .verifyComplete();
    }

    @Test
    void getSentNotificationError() {
        when(senderReadB2BApi.getSentNotificationV23(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()))
                .thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath(path)
                )
                .respond(response()
                        .withStatusCode(404)
                );


        StepVerifier.create(paDeliveryClient.getSentNotification(
                        xPagopaPnUid,
                        xPagopaPnCxType,
                        xPagopaPnCxId,
                        iun,
                        xPagopaPnCxGroups
                ))
                .expectError()
                .verify();
    }
}