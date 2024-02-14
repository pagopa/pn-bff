package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
@ContextConfiguration(classes = {PnDeliveryClientRecipientImpl.class})
@TestPropertySource(properties = {
        "pn.bff.delivery-push-base-url=http://localhost:9998",
})
public class PnDeliveryClientRecipientTestIT {
    @Autowired
    private PnDeliveryClientRecipientImpl pnDeliveryClient;
    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi")
    private RecipientReadApi recipientReadApi;
    private static ClientAndServer mockServer;
    final String xPagopaPnUid = "userId";
    final CxTypeAuthFleet xPagopaPnCxType = CxTypeAuthFleet.PA;
    final String xPagopaPnCxId = "cxId";
    final String iun = "DHUJ-QYVT-DMVH-202302-P-1";
    final List<String> xPagopaPnCxGroups = Collections.singletonList("group");
    final String mandateId = "MANDATE_ID";
    final String path = "/delivery/v2.3/notifications/received/" + iun;

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

        FullReceivedNotificationV23 mockNotification = new FullReceivedNotificationV23();

        when(recipientReadApi.getReceivedNotificationV23(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString()))
                .thenReturn(Mono.just(mockNotification));

        // When
        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath(path)
                )
                .respond(response()
                        .withStatusCode(200)
                );

        StepVerifier.create(pnDeliveryClient.getReceivedNotification(
                        xPagopaPnUid,
                        xPagopaPnCxType,
                        xPagopaPnCxId,
                        iun,
                        xPagopaPnCxGroups,
                        mandateId
                ))
                .expectNext(mockNotification)
                .verifyComplete();
    }

    @Test
    void getSentNotificationV23Error() {
        when(recipientReadApi.getReceivedNotificationV23(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList(),
                Mockito.anyString())).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        StepVerifier.create(pnDeliveryClient.getReceivedNotification(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups,
                mandateId
        )).expectError(PnBffException.class).verify();
    }
}
