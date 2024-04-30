package it.pagopa.pn.bff.pnclient.deliverypush;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.DocumentsWebApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.DocumentDownloadMetadataResponse;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnDocumentsWebClientImpl {
    private final DocumentsWebApi documentsWebApi;

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
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}