package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPaSummary;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPgGroupStatus;
import it.pagopa.pn.bff.mappers.inforecipient.GroupsMapper;
import it.pagopa.pn.bff.mappers.inforecipient.PaListMapper;
import it.pagopa.pn.bff.pnclient.externalregistries.PnExternalRegistriesClientImpl;
import it.pagopa.pn.bff.utils.PnBffExceptionUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;

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
     * @param paNameFilter The prefix of the PA name
     * @param id           The id of the PA
     * @return The list of PAs
     */
    public Flux<BffPaSummary> getPaList(String paNameFilter, List<String> id) {
        log.info("getPaList");

        return pnExternalRegistriesClient.getPaList(paNameFilter, id)
                .onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException)
                .map(PaListMapper.modelMapper::mapPaList);
    }
}