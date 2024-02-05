package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PnDeliveryClientImpl {

    private final RecipientReadApi recipientReadApi;

    public Mono<FullReceivedNotificationV23> retrieveNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                  String xPagopaPnCxId, String iun,
                                                                  List<String> xPagopaPnCxGroups, String mandateId) {
        return recipientReadApi.getReceivedNotificationV23(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups,
                mandateId
        );
    }

    ;
}
