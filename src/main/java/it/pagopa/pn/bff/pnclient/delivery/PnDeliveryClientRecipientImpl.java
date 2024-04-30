package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
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

    public Mono<NotificationSearchResponse> searchReceivedNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                       String xPagopaPnCxId, String iunMatch, List<String> xPagopaPnCxGroups,
                                                                       String mandateId, String senderId, NotificationStatus status, OffsetDateTime startDate, OffsetDateTime endDate,
                                                                       String subjectRegExp,
                                                                       int size, String nextPagesKey) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "searchReceivedNotification");
        Mono<NotificationSearchResponse> deliveryNotification;

        deliveryNotification = recipientReadApi.searchReceivedNotification(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                startDate,
                endDate,
                xPagopaPnCxGroups,
                mandateId,
                senderId,
                status,
                subjectRegExp,
                iunMatch,
                size,
                nextPagesKey
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return deliveryNotification;
    }

    public Mono<NotificationSearchResponse> searchReceivedDelegatedNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                               String xPagopaPnCxId, String iunMatch, List<String> xPagopaPnCxGroups,
                                                                               String senderId, String recipientId, String group, NotificationStatus status,
                                                                               OffsetDateTime startDate, OffsetDateTime endDate, int size, String nextPagesKey) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "searchReceivedDelegatedNotification");
        Mono<NotificationSearchResponse> deliveryNotification;

        deliveryNotification = recipientReadApi.searchReceivedDelegatedNotification(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                startDate,
                endDate,
                xPagopaPnCxGroups,
                senderId,
                recipientId,
                group,
                iunMatch,
                status,
                size,
                nextPagesKey
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        return deliveryNotification;
    }
}
