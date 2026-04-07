package it.pagopa.pn.bff.pnclient.notificationcostservice;

import it.pagopa.pn.bff.generated.openapi.msclient.notification_cost_service.api.NotificationCostRecipientApi;
import it.pagopa.pn.bff.generated.openapi.msclient.notification_cost_service.model.NotificationCostRecipientResponse;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnNotificationCostServiceClientImpl {

    private final NotificationCostRecipientApi notificationCostRecipientApi;

    public Mono<NotificationCostRecipientResponse> getNotificationCostRecipient(String iun, Integer recIndex) {
        log.logInvokingExternalService("pn-notification-cost-service", "getNotificationCost");

        return notificationCostRecipientApi.getNotificationCost(iun, recIndex);
    }
}