package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {PnDeliveryClientRecipientImpl.class})
@TestPropertySource(properties = {
        "pn.bff.delivery-push-base-url=http://localhost:9998",
})
public class PnDeliveryClientRecipientTestIT {
    @Autowired
    private PnDeliveryClientRecipientImpl deliveryClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi")
    private RecipientReadApi recipientReadApi;
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
    void getReceivedNotification() {
        String path = "/bff/notifications/received/DHUJ-QYVT-DMVH-202302-P-1";

        // When
        new MockServerClient("localhost", 9998)
                .when(request()
                        .withMethod("GET")
                        .withPath(path)
                )
                .respond(response()
                        .withStatusCode(200)
                );

        deliveryClient.getReceivedNotification(
                "Uid",
                CxTypeAuthFleet.PF,
                "CxId",
                "DHUJ-QYVT-DMVH-202302-P-1",
                null,
                "MANDATE_ID"
        );

        // Then
        assertDoesNotThrow(() -> {
            deliveryClient.getReceivedNotification(
                    "Uid",
                    CxTypeAuthFleet.PF,
                    "CxId",
                    "DHUJ-QYVT-DMVH-202302-P-1",
                    null,
                    "MANDATE_ID"
            );
        });
    }
}
