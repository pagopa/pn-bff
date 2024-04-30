package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

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

        return recipientReadApi.getReceivedNotificationV23(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups,
                mandateId
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    public Mono<NotificationAttachmentDownloadMetadataResponse> getReceivedNotificationDocument(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                                String xPagopaPnCxId, String iun, Integer docIdx,
                                                                                                List<String> xPagopaPnCxGroups, UUID mandateId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedNotificationDocument");

        return recipientReadApi.getReceivedNotificationDocument(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                docIdx,
                xPagopaPnCxGroups,
                mandateId
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}