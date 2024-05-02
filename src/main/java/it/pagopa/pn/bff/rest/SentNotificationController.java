package it.pagopa.pn.bff.rest;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.server.v1.api.NotificationSentApi;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.bff.service.NotificationsPAService;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@CustomLog
@RestController
public class SentNotificationController implements NotificationSentApi {

    private final NotificationsPAService notificationsPAService;

    public SentNotificationController(NotificationsPAService notificationsPAService) {
        this.notificationsPAService = notificationsPAService;
    }

    /**
     * GET bff/v1/notifications/sent/{iun}: Notification detail
     * Get the detail of a notification. This is for a Public Administration user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnCxId     Public Administration id
     * @param iun               Notification IUN
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param exchange
     * @return the detail of the notification with a specific IUN
     */
    @Override
    public Mono<ResponseEntity<BffFullNotificationV1>> getSentNotificationV1(String xPagopaPnUid,
                                                                             CxTypeAuthFleet xPagopaPnCxType,
                                                                             String xPagopaPnCxId,
                                                                             String iun,
                                                                             List<String> xPagopaPnCxGroups,
                                                                             final ServerWebExchange exchange) {
        log.logStartingProcess("getSentNotificationV1");
        Mono<BffFullNotificationV1> serviceResponse = notificationsPAService.getSentNotificationDetail(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        log.logEndingProcess("getSentNotificationV1");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    /**
     * GET bff/v1/notifications/sent/{iun}/documents: Notification document
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
    public Mono<ResponseEntity<BffDocumentDownloadMetadataResponse>> getSentNotificationDocumentV1(String xPagopaPnUid,
                                                                                                   CxTypeAuthFleet xPagopaPnCxType,
                                                                                                   String xPagopaPnCxId,
                                                                                                   String iun,
                                                                                                   BffDocumentType documentType,
                                                                                                   DocumentId documentId,
                                                                                                   List<String> xPagopaPnCxGroups,
                                                                                                   LegalFactCategory legalFactCategory,
                                                                                                   final ServerWebExchange exchange) {
        log.logStartingProcess("getSentNotificationDocumentV1");
        Mono<BffDocumentDownloadMetadataResponse> serviceResponse = notificationsPAService.getSentNotificationDocument(
                xPagopaPnUid, xPagopaPnCxType, xPagopaPnCxId, iun, documentId, documentType,
                legalFactCategory, xPagopaPnCxGroups
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);

        log.logEndingProcess("getSentNotificationDocumentV1");
        return serviceResponse.map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}