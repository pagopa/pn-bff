package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPaGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaGroup;
import it.pagopa.pn.bff.mappers.groups.GroupsMapper;
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
public class GroupsService {
    private final PnExternalRegistriesClientImpl pnExternalRegistriesClient;
    private final PnBffExceptionUtility pnBffExceptionUtility;

    /**
     * Get the list of groups for the user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxGroups  Public Administration Group id List
     * @param statusFilter    Filter for the status of the groups
     * @return the list of groups
     */
    public Flux<PaGroup> getGroups(String xPagopaPnUid, String xPagopaPnCxId, List<String> xPagopaPnCxGroups, BffPaGroupStatus statusFilter){
        log.info("getGroups");

        Flux<it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup> paGroups = pnExternalRegistriesClient.getGroups(
                xPagopaPnUid,
                xPagopaPnCxId,
                xPagopaPnCxGroups,
                null
        ).onErrorMap(WebClientResponseException.class, pnBffExceptionUtility::wrapException);

        return paGroups.map(GroupsMapper.modelMapper::mapGroups);
    }
}
