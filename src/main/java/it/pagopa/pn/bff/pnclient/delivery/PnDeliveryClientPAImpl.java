package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnDeliveryClientPAImpl {

    private final SenderReadB2BApi senderReadB2BApi;

    public Mono<FullSentNotificationV23> getSentNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                             String xPagopaPnCxId, String iun,
                                                             List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getSentNotificationV23");
        return senderReadB2BApi.getSentNotificationV23(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups
        );
    }

}
