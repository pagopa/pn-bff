package it.pagopa.pn.bff.pnclient.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.api.MandateServiceApi;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateCountsDto;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@CustomLog
@RequiredArgsConstructor
public class PnMandateClientRecipientImpl {
    private final MandateServiceApi mandateApi;

    public Mono<MandateCountsDto> countMandatesByDelegate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType,
                                                          List<String> xPagopaPnCxGroups, String xPagopaPnCxRole,
                                                          String status) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_MANDATE, "countMandatesByDelegate");

        return mandateApi.countMandatesByDelegate(
                xPagopaPnCxId,
                xPagopaPnCxType,
                xPagopaPnCxGroups,
                xPagopaPnCxRole,
                status
        );
    }
}