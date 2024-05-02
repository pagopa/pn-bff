package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.api.SenderReadWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatus;
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
public class PnDeliveryClientPAImpl {

    private final SenderReadB2BApi senderReadB2BApi;
    private final SenderReadWebApi senderReadWebApi;

    public Mono<NotificationSearchResponse> searchSentNotifications(String xPagopaPnUid, it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet xPagopaPnCxType,
                                                                    String xPagopaPnCxId, OffsetDateTime startDate,
                                                                    OffsetDateTime endDate, List<String> xPagopaPnCxGroups,
                                                                    String recipientId, NotificationStatus status, String subjectRegExp,
                                                                    String iunMatch, Integer size, String nextPagesKey) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "searchSentNotification");

        return senderReadWebApi.searchSentNotification(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                startDate,
                endDate,
                xPagopaPnCxGroups,
                recipientId,
                status,
                subjectRegExp,
                iunMatch,
                size,
                nextPagesKey
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

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
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }

    public Mono<NotificationAttachmentDownloadMetadataResponse> getSentNotificationDocument(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                            String xPagopaPnCxId, String iun, Integer docIdx,
                                                                                            List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getSentNotificationDocument");

        return senderReadB2BApi.getSentNotificationDocument(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                docIdx,
                xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}