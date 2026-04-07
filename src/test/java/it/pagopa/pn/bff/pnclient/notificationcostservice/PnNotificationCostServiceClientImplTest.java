package it.pagopa.pn.bff.pnclient.notificationcostservice;

import it.pagopa.pn.bff.generated.openapi.msclient.notification_cost_service.api.NotificationCostRecipientApi;
import it.pagopa.pn.bff.generated.openapi.msclient.notification_cost_service.model.NotificationCostRecipientResponse;
import it.pagopa.pn.bff.mocks.NotificationCostDetailsMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PnNotificationCostServiceClientImpl.class})
@ExtendWith(SpringExtension.class)
class PnNotificationCostServiceClientImplTest {

    private final NotificationCostDetailsMock notificationCostMock = new NotificationCostDetailsMock();

    @Autowired
    private PnNotificationCostServiceClientImpl pnNotificationCostServiceClient;

    @MockBean(name = "it.pagopa.pn.bff.generated.openapi.msclient.notification_cost_service.api.NotificationCostRecipientApi")
    private NotificationCostRecipientApi notificationCostRecipientApi;

    @Test
    void testGetNotificationCostRecipient() {
        when(notificationCostRecipientApi.getNotificationCost(anyString(), anyInt()))
                .thenReturn(Mono.just(notificationCostMock.getNotificationCostRecipientResponseMock()));

        Mono<NotificationCostRecipientResponse> result = pnNotificationCostServiceClient
                .getNotificationCostRecipient("test-iun", 0);

        StepVerifier.create(result)
                .expectNext(notificationCostMock.getNotificationCostRecipientResponseMock())
                .verifyComplete();
    }

    @Test
    void testGetNotificationCostRecipientError() {
        when(notificationCostRecipientApi.getNotificationCost(anyString(), anyInt()))
                .thenReturn(Mono.error(new RuntimeException("Error")));

        Mono<NotificationCostRecipientResponse> result = pnNotificationCostServiceClient
                .getNotificationCostRecipient("test-iun", 0);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}