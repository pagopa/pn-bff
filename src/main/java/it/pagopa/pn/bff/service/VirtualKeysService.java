package it.pagopa.pn.bff.service;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VirtualKeysService {

    /**
     * Get a paginated list of the api keys that belong to a Public Administration and are accessible by the current user
     *
     * @param xPagopaPnUid      User Identifier
     * @param xPagopaPnCxType   Public Administration Type
     * @param xPagopaPnRole     Public Administration Role
     * @param xPagopaPnCxId     Public Administration id
     * @param xPagopaPnCxGroups Public Administration Group id List
     * @param id                Virtual Keys id
     * @return
     */
    public Mono<BffApiKeysResponse> deleteVirtualKeys(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType,
                                               String xPagopaPnCxId, List<String> xPagopaPnCxGroups,
                                               String id, String xPagopaPnRole
    ){return null;}
}
