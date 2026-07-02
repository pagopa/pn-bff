package it.pagopa.pn.bff.pnclient.delivery;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.api.RecipientReadInformalNotificationApi;
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
    private final RecipientReadInformalNotificationApi recipientReadInformalNotificationApi;

    public Mono<FullNotificationSearchResponse> searchReceivedNotifications(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                            String xPagopaPnCxId, String iunMatch, List<String> xPagopaPnCxGroups,
                                                                            String mandateId, String senderId, OffsetDateTime startDate, OffsetDateTime endDate,
                                                                            String subjectRegExp,
                                                                            int size, String nextPagesKey, String communicationType) {
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
                subjectRegExp,
                iunMatch,
                size,
                nextPagesKey,
                communicationType
        );
    }

    public Mono<LegalNotificationSearchResponse> searchReceivedDelegatedNotifications(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
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

    public Mono<FullReceivedNotificationV28> getReceivedNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                                                     String xPagopaPnCxId, String xPagopaPnSrcCh, String iun,
                                                                     List<String> xPagopaPnCxGroups, String xPagopaPnSrcChDetails, String mandateId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedNotificationV28");

        return recipientReadApi.getReceivedNotificationV28(
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

    public Mono<FullReceivedInformalNotificationV1> getReceivedInformalNotification(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String xPagopaPnSrcCh,
            String iun,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnSrcChDetails
    ) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedInformalNotificationV1");

        return recipientReadInformalNotificationApi.getReceivedInformalNotificationV1(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails
        );
    }

    public Mono<NotificationAttachmentDownloadMetadataResponse> getReceivedInformalNotificationDocument(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String xPagopaPnSrcCh,
            String iun,
            Integer docIdx,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnSrcChDetails
    ) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedInformalNotificationDocumentV1");

        return recipientReadInformalNotificationApi.getReceivedInformalNotificationDocumentV1(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                docIdx,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails
        );
    }

    public Mono<NotificationAttachmentDownloadMetadataResponse> getReceivedInformalNotificationPaymentAttachment(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String xPagopaPnSrcCh,
            String iun,
            String attachmentName,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnSrcChDetails,
            Integer attachmentIdx
    ) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY, "getReceivedInformalNotificationAttachmentV1");

        return recipientReadInformalNotificationApi.getReceivedInformalNotificationAttachmentV1(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                attachmentName,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails,
                attachmentIdx
        );
    }
}