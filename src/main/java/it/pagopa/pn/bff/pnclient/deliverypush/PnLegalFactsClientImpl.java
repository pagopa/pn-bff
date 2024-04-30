package it.pagopa.pn.bff.pnclient.deliverypush;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.api.LegalFactsApi;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactCategory;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.LegalFactDownloadMetadataResponse;
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
public class PnLegalFactsClientImpl {
    private final LegalFactsApi legalFactsApi;

    public Mono<LegalFactDownloadMetadataResponse> getLegalFact(String xPagopaPnUid,
                                                                CxTypeAuthFleet xPagopaPnCxType,
                                                                String xPagopaPnCxId,
                                                                String iun,
                                                                LegalFactCategory legalFactType,
                                                                String legalFactId,
                                                                List<String> xPagopaPnCxGroups,
                                                                UUID mandateId) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_DELIVERY_PUSH, "getLegalFact");

        return legalFactsApi.getLegalFact(
                xPagopaPnUid,
                xPagopaPnCxType,
                xPagopaPnCxId,
                iun,
                legalFactType,
                legalFactId,
                xPagopaPnCxGroups,
                mandateId
        ).onErrorMap(WebClientResponseException.class, PnBffException::wrapException);
    }
}