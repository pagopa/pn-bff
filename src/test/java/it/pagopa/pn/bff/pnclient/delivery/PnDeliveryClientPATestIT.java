package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@ContextConfiguration(classes = {PnDeliveryClientPAImpl.class})
@TestPropertySource(properties = {
        "pn.bff.delivery-push-base-url=http://localhost:9998",
})
public class PnDeliveryClientPATestIT {
    @Autowired
    private PnDeliveryClientPAImpl paDeliveryClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi")
    private SenderReadB2BApi senderReadB2BApi;
    private static ClientAndServer mockServer;

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
        String path = "/bff/notifications/sent/DHUJ-QYVT-DMVH-202302-P-1";

        // When
        new MockServerClient("localhost", 9998)
                .when(request()
                        .withMethod("GET")
                        .withPath(path)
                )
                .respond(response()
                        .withStatusCode(200)
                );

        paDeliveryClient.getSentNotification(
                "Uid",
                CxTypeAuthFleet.PA,
                "CxId",
                "DHUJ-QYVT-DMVH-202302-P-1",
                null
        );

        // Then
        assertDoesNotThrow(() -> {
            paDeliveryClient.getSentNotification(
                    "Uid",
                    CxTypeAuthFleet.PF,
                    "CxId",
                    "DHUJ-QYVT-DMVH-202302-P-1",
                    null
            );
        });
    }
}
