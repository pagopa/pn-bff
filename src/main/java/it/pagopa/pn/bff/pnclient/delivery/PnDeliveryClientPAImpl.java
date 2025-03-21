package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.NewNotificationApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.api.SenderReadB2BApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.*;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.api.SenderReadWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatusV26;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV27;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnDeliveryClientPAImpl {

    private final SenderReadB2BApi senderReadB2BApi;
    private final SenderReadWebApi senderReadWebApi;
    private final NewNotificationApi newNotificationApi;

    public Mono<NotificationSearchResponse> searchSentNotifications(String xPagopaPnUid, it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet xPagopaPnCxType,
                                                                    String xPagopaPnCxId, OffsetDateTime startDate,
                                                                    OffsetDateTime endDate, List<String> xPagopaPnCxGroups,
                                                                    String recipientId, NotificationStatusV26 status, String subjectRegExp,
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
        );
    }

    public Mono<FullSentNotificationV27> getSentNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                             String xPagopaPnCxId, String iun,
                                                             List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getSentNotificationV25");

        return senderReadB2BApi.getSentNotificationV27(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups
        );
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
        );
    }

    public Mono<NotificationAttachmentDownloadMetadataResponse> getSentNotificationPayment(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                           String xPagopaPnCxId, String iun, Integer recipientIdx,
                                                                                           String attachmentName, List<String> xPagopaPnCxGroups,
                                                                                           Integer attachmentIdx) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getSentNotificationAttachment");

        return senderReadB2BApi.getSentNotificationAttachment(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                recipientIdx,
                attachmentName,
                xPagopaPnCxGroups,
                attachmentIdx
        );
    }

    public Mono<NewNotificationResponse> newSentNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                             String xPagopaPnCxId, NewNotificationRequestV25 NewNotificationRequestV25,
                                                             List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "sendNewNotificationV23");

        return newNotificationApi.sendNewNotificationV25(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                "WEB",
                NewNotificationRequestV25,
                xPagopaPnCxGroups,
                null,
                null
        );
    }

    public Flux<PreLoadResponse> preSignedUpload(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                 String xPagopaPnCxId, List<PreLoadRequest> preLoadRequest) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "presignedUploadRequest");

        return newNotificationApi.presignedUploadRequest(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                preLoadRequest
        );
    }
}