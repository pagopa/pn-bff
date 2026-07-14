package it.pagopa.pn.bff.pnclient.deliverypush;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.DocumentsWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.LegalFactsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.NotificationCancellationApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.*;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push_rework.api.NotificationReworkApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push_rework.model.ReworkItemsResponse;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnDeliveryPushClientImpl {
    private final DocumentsWebApi documentsWebApi;
    private final LegalFactsApi legalFactsApi;
    private final NotificationCancellationApi notificationCancellationApi;
    private final NotificationReworkApi notificationReworkApi;

    public Mono<DocumentDownloadMetadataResponse> getDocumentsWeb(String xPagopaPnUid,
                                                                  CxTypeAuthFleet xPagopaPnCxType,
                                                                  String xPagopaPnCxId,
                                                                  String iun,
                                                                  DocumentCategory documentType,
                                                                  String documentId,
                                                                  List<String> xPagopaPnCxGroups,
                                                                  UUID mandateId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY_PUSH, "getDocumentsWeb");

        return documentsWebApi.getDocumentsWeb(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                documentType,
                documentId,
                xPagopaPnCxGroups,
                mandateId
        );
    }

    public Mono<LegalFactDownloadMetadataResponse> getLegalFact(String xPagopaPnUid,
                                                                CxTypeAuthFleet xPagopaPnCxType,
                                                                String xPagopaPnCxId,
                                                                String iun,
                                                                String legalFactId,
                                                                List<String> xPagopaPnCxGroups,
                                                                UUID mandateId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY_PUSH, "getLegalFact");

        return legalFactsApi.getLegalFactById(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                legalFactId,
                xPagopaPnCxGroups,
                mandateId
        );
    }

    public Mono<RequestStatus> notificationCancellation(String xPagopaPnUid,
                                                        CxTypeAuthFleet xPagopaPnCxType,
                                                        String xPagopaPnCxId,
                                                        String iun,
                                                        List<String> xPagopaPnCxGroups
    ) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY_PUSH, "notificationCancellation");

        return notificationCancellationApi.notificationCancellation(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                xPagopaPnCxGroups);
    }

    /**
     * Retrieve the list of timeline corrections (rework) for a notification.
     *
     * @param iun Notification IUN
     * @return the rework items of the notification
     */
    public Mono<ReworkItemsResponse> getRework(String iun) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY_PUSH, "retrieveNotificationRework");

        return notificationReworkApi.retrieveNotificationRework(iun, null);
    }
}