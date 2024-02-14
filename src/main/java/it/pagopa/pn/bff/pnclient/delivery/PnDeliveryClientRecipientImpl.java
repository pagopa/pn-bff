package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnDeliveryClientRecipientImpl {

    private final RecipientReadApi recipientReadApi;

    public Mono<FullReceivedNotificationV23> getReceivedNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                     String xPagopaPnCxId, String iun,
                                                                     List<String> xPagopaPnCxGroups, String mandateId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedNotificationV23");
        Mono<FullReceivedNotificationV23> deliveryNotification;

        deliveryNotification = recipientReadApi.getReceivedNotificationV23(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups,
                mandateId
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return deliveryNotification;
    }
}
