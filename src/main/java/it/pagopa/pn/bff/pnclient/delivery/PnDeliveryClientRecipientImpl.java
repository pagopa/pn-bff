package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.*;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnDeliveryClientRecipientImpl {

    private final RecipientReadApi recipientReadApi;

    public Mono<NotificationSearchResponse> searchReceivedNotifications(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                        String xPagopaPnCxId, String iunMatch, List<String> xPagopaPnCxGroups,
                                                                        String mandateId, String senderId, NotificationStatusV26 status, OffsetDateTime startDate, OffsetDateTime endDate,
                                                                        String subjectRegExp,
                                                                        int size, String nextPagesKey) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "searchReceivedNotification");

        return recipientReadApi.searchReceivedNotification(
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
        );
    }

    public Mono<NotificationSearchResponse> searchReceivedDelegatedNotifications(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                 String xPagopaPnCxId, String iunMatch, List<String> xPagopaPnCxGroups,
                                                                                 String senderId, String recipientId, String group, NotificationStatusV26 status,
                                                                                 OffsetDateTime startDate, OffsetDateTime endDate, int size, String nextPagesKey) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "searchReceivedDelegatedNotification");

        return recipientReadApi.searchReceivedDelegatedNotification(
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
        );
    }

    public Mono<FullReceivedNotificationV25> getReceivedNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                     String xPagopaPnCxId, String xPagopaPnSrcCh, String iun,
                                                                     List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails, String mandateId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedNotificationV23");

        return recipientReadApi.getReceivedNotificationV25(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails,
                mandateId
        );
    }

    public Mono<NotificationAttachmentDownloadMetadataResponse> getReceivedNotificationDocument(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                                String xPagopaPnCxId, String xPagopaPnSrcCh, String iun, Integer docIdx,
                                                                                                List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails, UUID mandateId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedNotificationDocument");

        return recipientReadApi.getReceivedNotificationDocument(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                docIdx,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails,
                mandateId
        );
    }

    public Mono<NotificationAttachmentDownloadMetadataResponse> getReceivedNotificationPayment(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                               String xPagopaPnCxId, String xPagopaPnSrcCh, String iun, String attachmentName,
                                                                                               List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails, UUID mandateId,
                                                                                               Integer attachmentIdx) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedNotificationAttachment");

        return recipientReadApi.getReceivedNotificationAttachment(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                attachmentName,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails,
                mandateId,
                attachmentIdx
        );
    }

    public Mono<ResponseCheckAarMandateDto> checkAarQrCode(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                           String xPagopaPnCxId,
                                                           RequestCheckAarMandateDto requestCheckAarMandateDto,
                                                           List<String> xPagopaPnCxGroups) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "checkAarQrCode");

        return recipientReadApi.checkAarQrCode(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                requestCheckAarMandateDto,
                xPagopaPnCxGroups
        );
    }
}