package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationAttachmentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet;
import it.pagopa.pn.bff.mappers.CxTypeMapper;
import it.pagopa.pn.bff.mappers.notifications.InformalNotificationReceivedMapper;
import it.pagopa.pn.bff.mappers.notifications.NotificationDownloadDocumentMapper;
import it.pagopa.pn.bff.pnclient.delivery.PnDeliveryClientRecipientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InformalNotificationRecipientService {

    private final PnDeliveryClientRecipientImpl pnDeliveryClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get the detail of an informal notification. This is for a recipient user.
     *
     * @param xPagopaPnUid          User Identifier
     * @param xPagopaPnCxType       Receiver Type
     * @param xPagopaPnCxId         Receiver id
     * @param xPagopaPnSrcCh        User login source channel
     * @param iun                   Informal Notification IUN
     * @param xPagopaPnCxGroups     Receiver Group id List
     * @param xPagopaPnSrcChDetails User login source channel details
     * @return the detail of the informal notification with a specific IUN
     */
    public Mono<BffFullInformalNotificationV1> getInformalNotificationDetail(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String xPagopaPnSrcCh,
            String iun,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnSrcChDetails) {
        log.info("Get informal notification detail - user id: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        Mono<FullReceivedInformalNotificationV1> informalNotification = pnDeliveryClient.getReceivedInformalNotification(
                xPagopaPnUid,
                CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return informalNotification.map(InformalNotificationReceivedMapper.modelMapper::mapReceivedInformalNotificationDetail);
    }

    /**
     * Get the attached documents of an informal notification
     *
     * @param xPagopaPnUid          User Identifier
     * @param xPagopaPnCxType       Receiver Type
     * @param xPagopaPnCxId         Receiver id
     * @param xPagopaPnSrcCh        User login source channel
     * @param iun                   Informal Notification IUN
     * @param docIdx                The document index
     * @param xPagopaPnCxGroups     Receiver Group id List
     * @param xPagopaPnSrcChDetails User login source channel details
     * @return the requested attached document
     */
    public Mono<BffDocumentDownloadMetadataResponse> getInformalNotificationDocument(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String xPagopaPnSrcCh,
            String iun,
            Integer docIdx,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnSrcChDetails
    ) {
        log.info("Get informal notification document - user id: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        Mono<NotificationAttachmentDownloadMetadataResponse> informalNotificationDocument = pnDeliveryClient
                .getReceivedInformalNotificationDocument(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                        xPagopaPnCxId,
                        xPagopaPnSrcCh,
                        iun,
                        docIdx,
                        xPagopaPnCxGroups,
                        xPagopaPnSrcChDetails
                ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return informalNotificationDocument.map(
                NotificationDownloadDocumentMapper.modelMapper::mapReceivedAttachmentDownloadResponse
        );
    }

    /**
     * Get the attachment document of a payment for an informal notification
     *
     * @param xPagopaPnUid          User Identifier
     * @param xPagopaPnCxType       Receiver Type
     * @param xPagopaPnCxId         Receiver id
     * @param xPagopaPnSrcCh        User login source channel
     * @param iun                   Informal Notification IUN
     * @param attachmentName        Type of the payment (PAGOPA or F24)
     * @param xPagopaPnCxGroups     Receiver Group id List
     * @param xPagopaPnSrcChDetails User login source channel details
     * @param attachmentIdx         Index of the payment
     * @return the requested payment document
     */
    public Mono<BffDocumentDownloadMetadataResponse> getInformalNotificationDocument(
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
        log.info("Get informal notification payment attachment - user id: {} - type: {} - groups: {} - iun: {}",
                xPagopaPnCxId, xPagopaPnCxType, xPagopaPnCxGroups, iun);

        Mono<NotificationAttachmentDownloadMetadataResponse> informalNotificationPaymentAttachment = pnDeliveryClient
                .getReceivedInformalNotificationPaymentAttachment(
                        xPagopaPnUid,
                        CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(xPagopaPnCxType),
                        xPagopaPnCxId,
                        xPagopaPnSrcCh,
                        iun,
                        attachmentName,
                        xPagopaPnCxGroups,
                        xPagopaPnSrcChDetails,
                        attachmentIdx
                ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return informalNotificationPaymentAttachment.map(
                NotificationDownloadDocumentMapper.modelMapper::mapReceivedAttachmentDownloadResponse
        );
    }
}
