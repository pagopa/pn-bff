package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.generated.openapi.server.v1.api.RecipientInformalNotificationsApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffDocumentDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet;
import it.pagopa.pn.bff.service.InformalNotificationRecipientService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class InformalNotificationRecipientController implements RecipientInformalNotificationsApi {
    private final InformalNotificationRecipientService informalNotificationRecipientService;

    public InformalNotificationRecipientController(InformalNotificationRecipientService informalNotificationRecipientService) {
        this.informalNotificationRecipientService = informalNotificationRecipientService;
    }

    /**
     * GET /bff/v1/notifications/informal/received/{iun}: Informal Notification detail
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
    @Override
    public Mono<ResponseEntity<BffFullInformalNotificationV1>> getReceivedInformalNotificationV1(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String xPagopaPnSrcCh,
            String iun,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnSrcChDetails,
            final ServerWebExchange exchange) {

        Mono<BffFullInformalNotificationV1> informalNotification = informalNotificationRecipientService.getInformalNotificationDetail(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                xPagopaPnSrcCh,
                iun,
                xPagopaPnCxGroups,
                xPagopaPnSrcChDetails
        );

        return informalNotification.map(response ->
                ResponseEntity.status(HttpStatus.OK).body(response)
        );
    }

    /**
     * GET /bff/v1/notifications/informal/received/{iun}/attachments/documents/{docIdx}
     * Download the documents linked to an informal notification
     *
     * @param xPagopaPnUid          User Identifier
     * @param xPagopaPnCxType       Receiver Type
     * @param xPagopaPnCxId         Receiver id
     * @param xPagopaPnSrcCh        User login source channel
     * @param iun                   Informal Notification IUN
     * @param docIdx                The document index
     * @param xPagopaPnCxGroups     Receiver Group id List
     * @param xPagopaPnSrcChDetails User login source channel details
     * @return the requested document
     */
    @Override
    public Mono<ResponseEntity<BffDocumentDownloadMetadataResponse>> getReceivedInformalNotificationDocumentV1(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String xPagopaPnSrcCh,
            String iun,
            Integer docIdx,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnSrcChDetails,
            final ServerWebExchange exchange
    ) {
        Mono<BffDocumentDownloadMetadataResponse> informalNotificationDocument = informalNotificationRecipientService
                .getInformalNotificationDocument(
                        xPagopaPnUid,
                        xPagopaPnCxType,
                        xPagopaPnCxId,
                        xPagopaPnSrcCh,
                        iun,
                        docIdx,
                        xPagopaPnCxGroups,
                        xPagopaPnSrcChDetails
                );

        return informalNotificationDocument.map(response ->
                ResponseEntity.status(HttpStatus.OK).body(response)
        );
    }

    /**
     * GET /bff/v1/notifications/informal/received/{iun}/attachments/payment/{attachmentName}
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
    public Mono<ResponseEntity<BffDocumentDownloadMetadataResponse>> getReceivedInformalNotificationPaymentAttachmentV1(
            String xPagopaPnUid,
            CxTypeAuthFleet xPagopaPnCxType,
            String xPagopaPnCxId,
            String xPagopaPnSrcCh,
            String iun,
            String attachmentName,
            List<String> xPagopaPnCxGroups,
            String xPagopaPnSrcChDetails,
            Integer attachmentIdx,
            final ServerWebExchange exchange
    ) {
        Mono<BffDocumentDownloadMetadataResponse> informalNotificationPaymentAttachment = informalNotificationRecipientService
                .getInformalNotificationDocument(
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

        return informalNotificationPaymentAttachment.map(response ->
                ResponseEntity.status(HttpStatus.OK).body(response)
        );
    }
}
