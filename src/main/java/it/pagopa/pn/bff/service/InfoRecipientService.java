package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.exceptions.PnBffException;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaSummary;
import it.pagopa.pn.bff.mappers.inforecipient.GroupsMapper;
import it.pagopa.pn.bff.mappers.inforecipient.PaListMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;

import static it.pagopa.pn.bff.exceptions.PnBffExceptionCodes.ERROR_CODE_BFF_INVALIDBODY;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoRecipientService {
    private final PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get the list of groups for the user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param status            Group status
     * @return the list of the groups or error
     */
    public Flux<BffPgGroup> getGroups(String xPagopaPnUid, String xPagopaPnCxId, List<String> xPagopaPnCxGroups, BffPgGroupStatus status) {
        log.info("getGroups");

        Flux<PgGroup> pgGroups = pnExternalRegistriesClient.getPgGroups(
                xPagopaPnUid,
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                GroupsMapper.modelMapper.mapGroupStatus(status)
        ).onErrorMap(
                WebClientResponseException.class,
                pnBffExceptionUtility::wrapException
        );

        return pgGroups.map(GroupsMapper.modelMapper::mapGroups);
    }

    /**
     * Get the list of PAs that use the PN
     *
     * @param xPagopaPnCxId   Public Administration id
     * @param xPagopaPnCxType The type of the user
     * @param paNameFilter    The prefix of the PA name
     * @return The list of PAs
     */
    public Flux<PaSummary> getPaList(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, String paNameFilter) {
        log.info("getPaList");

        if (!List.of(CxTypeAuthFleet.PF, CxTypeAuthFleet.PG).contains(xPagopaPnCxType)
                || xPagopaPnCxId.isEmpty()
        ) {
            return Flux.error(new PnBffException(
                    "Invalid request body",
                    "Invalid request body",
                    HttpStatus.BAD_REQUEST.value(),
                    ERROR_CODE_BFF_INVALIDBODY)
            );
        }

        return pnExternalRegistriesClient.getPaList(paNameFilter)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
                .map(PaListMapper.modelMapper::mapPaList);
    }
}