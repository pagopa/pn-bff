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
                                                                        String mandateId, String senderId, NotificationStatus status, OffsetDateTime startDate, OffsetDateTime endDate,
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
                                                                                 String senderId, String recipientId, String group, NotificationStatus status,
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

    public Mono<FullReceivedNotificationV23> getReceivedNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                     String xPagopaPnCxId, String iun,
                                                                     List<String> xPagopaPnCxGroups, String mandateId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedNotificationV23");

        return recipientReadApi.getReceivedNotificationV23(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups,
                mandateId
        );
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
        );
    }

    public Mono<NotificationAttachmentDownloadMetadataResponse> getReceivedNotificationPayment(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                                               String xPagopaPnCxId, String iun, String attachmentName,
                                                                                               List<String> xPagopaPnCxGroups, UUID mandateId,
                                                                                               Integer attachmentIdx) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedNotificationAttachment");

        return recipientReadApi.getReceivedNotificationAttachment(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                attachmentName,
                xPagopaPnCxGroups,
                mandateId,
                attachmentIdx
        );
    }

    public Mono<ResponseCheckAarMandateDto> checkAarQrCode(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                           String xPagopaPnCxId,
                                                           RequestCheckAarMandateDto requestCheckAarMandateDto,
                                                           List<String> xPagopaPnCxGroups){
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