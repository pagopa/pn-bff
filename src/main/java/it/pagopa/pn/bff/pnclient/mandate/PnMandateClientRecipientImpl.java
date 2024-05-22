package it.pagopa.pn.bff.pnclient.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.api.MandateServiceApi;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.*;
import it.pagopa.pn.commons.log.PnLogger;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
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

    public Mono<MandateDto> createMandate(String xPagopaPnUid,
                                          String xPagopaPnCxId,
                                          CxTypeAuthFleet xPagopaPnCxType,
                                          List<String> xPagopaPnCxGroups,
                                          String xPagopaPnCxRole,
                                          MandateDto mandateDto) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_MANDATE, "createMandate");

        return mandateApi.createMandate(
                xPagopaPnUid,
                xPagopaPnCxId,
                xPagopaPnCxType,
                xPagopaPnCxGroups,
                xPagopaPnCxRole,
                mandateDto
        );
    }

    public Mono<Void> acceptMandate(String xPagopaPnCxId,
                                    CxTypeAuthFleet xPagopaPnCxType,
                                    String mandateId,
                                    List<String> xPagopaPnCxGroups,
                                    String xPagopaPnCxRole,
                                    AcceptRequestDto acceptRequestDto) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_MANDATE, "acceptMandate");

        return mandateApi.acceptMandate(
                xPagopaPnCxId,
                xPagopaPnCxType,
                mandateId,
                xPagopaPnCxGroups,
                xPagopaPnCxRole,
                acceptRequestDto
        );
    }

    public Mono<Void> updateMandate(String xPagopaPnCxId,
                                    CxTypeAuthFleet xPagopaPnCxType,
                                    String mandateId,
                                    List<String> xPagopaPnCxGroups,
                                    String xPagopaPnCxRole,
                                    UpdateRequestDto updateRequestDto) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_MANDATE, "updateMandate");

        return mandateApi.updateMandate(
                xPagopaPnCxId,
                xPagopaPnCxType,
                mandateId,
                xPagopaPnCxGroups,
                xPagopaPnCxRole,
                updateRequestDto
        );
    }

    public Mono<Void> rejectMandate(String xPagopaPnCxId,
                                    CxTypeAuthFleet xPagopaPnCxType,
                                    String mandateId,
                                    List<String> xPagopaPnCxGroups,
                                    String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_MANDATE, "rejectMandate");

        return mandateApi.rejectMandate(
                xPagopaPnCxId,
                xPagopaPnCxType,
                mandateId,
                xPagopaPnCxGroups,
                xPagopaPnCxRole
        );
    }

    public Mono<Void> revokeMandate(String xPagopaPnCxId,
                                    CxTypeAuthFleet xPagopaPnCxType,
                                    String mandateId,
                                    List<String> xPagopaPnCxGroups,
                                    String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_MANDATE, "revokeMandate");

        return mandateApi.revokeMandate(
                xPagopaPnCxId,
                xPagopaPnCxType,
                mandateId,
                xPagopaPnCxGroups,
                xPagopaPnCxRole
        );
    }

    public Flux<MandateDto> getMandatesByDelegate(String xPagopaPnCxId,
                                                  CxTypeAuthFleet xPagopaPnCxType,
                                                  List<String> xPagopaPnCxGroups,
                                                  String xPagopaPnCxRole,
                                                  String status) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_MANDATE, "listMandatesByDelegate1");

        return mandateApi.listMandatesByDelegate1(
                xPagopaPnCxId,
                xPagopaPnCxType,
                xPagopaPnCxGroups,
                xPagopaPnCxRole,
                status
        );
    }

    public Mono<SearchMandateResponseDto> searchMandatesByDelegate(String xPagopaPnCxId,
                                                                   CxTypeAuthFleet xPagopaPnCxType,
                                                                   Integer size,
                                                                   List<String> xPagopaPnCxGroups,
                                                                   String xPagopaPnCxRole,
                                                                   String nextPageKey,
                                                                   SearchMandateRequestDto searchMandateRequestDto) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_MANDATE, "searchMandatesByDelegate");

        return mandateApi.searchMandatesByDelegate(
                xPagopaPnCxId,
                xPagopaPnCxType,
                size,
                xPagopaPnCxGroups,
                xPagopaPnCxRole,
                nextPageKey,
                searchMandateRequestDto
        );
    }

    public Flux<MandateDto> getMandatesByDelegator(String xPagopaPnCxId,
                                                   CxTypeAuthFleet xPagopaPnCxType,
                                                   List<String> xPagopaPnCxGroups,
                                                   String xPagopaPnCxRole) {
        log.logInvokingExternalService(PnLogger.EXTERNAL_SERVICES.PN_MANDATE, "listMandatesByDelegator1");

        return mandateApi.listMandatesByDelegator1(
                xPagopaPnCxId,
                xPagopaPnCxType,
                xPagopaPnCxGroups,
                xPagopaPnCxRole
        );
    }
}