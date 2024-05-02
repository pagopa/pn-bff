package it.pagopa.pn.bff.rest;


import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationReceivedApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.service.NotificationsRecipientService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@CustomLog
@RestController
public class ReceivedNotificationController implements NotificationReceivedApi {

    private final NotificationsRecipientService notificationsRecipientService;

    public ReceivedNotificationController(NotificationsRecipientService notificationsRecipientService) {
        this.notificationsRecipientService = notificationsRecipientService;
    }

    /**
     * GET bff/v1/notifications/received/{iun}: Notification detail
     * Get the detail of a notification. This is for a recipient user.
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Receiver Type
     * @param xPagopaPnCxId     Receiver id
     * @param iun               Notification IUN
     * @param xPagopaPnCxGroups Receiver Group id List
     * @param exchange
     * @param mandateId         mandate id. It is required if the user, that is requesting the notification, is a mandate
     * @return the detail of the notification with a specific IUN
     */
    @Override
    public Mono<ResponseEntity<BffFullNotificationV1>> getReceivedNotificationV1(String xPagopaPnUid,
                                                                                 CxTypeAuthFleet xPagopaPnCxType,
                                                                                 String xPagopaPnCxId,
                                                                                 String iun,
                                                                                 List<String> xPagopaPnCxGroups,
                                                                                 String mandateId,
                                                                                 final ServerWebExchange exchange) {
        log.logStartingProcess("getReceivedNotificationV1");

        Mono<BffFullNotificationV1> serviceResponse = notificationsRecipientService.getNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, xPagopaPnCxGroups, mandateId
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);


        log.logEndingProcess("getReceivedNotificationV1");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET bff/v1/notifications/received/{iun}/documents: Notification document
     * Download the document linked to a notification
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param documentId        the document id (safestorage key if aar or legalfact, the index in the array if attachment)
     * @param documentType      the document type (aar, attachment or legal fact)
     * @param legalFactCategory the legal fact category (required only if the documentType is legal fact)
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @return the requested document
     */
    @Override
    public Mono<ResponseEntity<BffDocumentDownloadMetadataResponse>> getReceivedNotificationDocumentV1(String xPagopaPnUid,
                                                                                                       CxTypeAuthFleet xPagopaPnCxType,
                                                                                                       String xPagopaPnCxId,
                                                                                                       String iun,
                                                                                                       BffDocumentType documentType,
                                                                                                       DocumentId documentId,
                                                                                                       List<String> xPagopaPnCxGroups,
                                                                                                       UUID mandateId,
                                                                                                       LegalFactCategory legalFactCategory,
                                                                                                       final ServerWebExchange exchange) {
        log.logStartingProcess("getReceivedNotificationDocumentV1");
        Mono<BffDocumentDownloadMetadataResponse> serviceResponse = notificationsRecipientService.getReceivedNotificationDocument(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, documentId, documentType,
                legalFactCategory, xPagopaPnCxGroups, mandateId
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        log.logEndingProcess("getReceivedNotificationDocumentV1");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}