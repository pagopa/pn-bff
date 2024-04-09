package it.pagopa.pn.bff.mappers.apikeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeyGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffApiKeysResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapstruct mapper interface, used to map the ApiKeysResponse
 * to the BffApiKeysResponse
 */
@Mapper
public abstract class ApiKeysMapper {
    // Instance of the mapper
    public static final ApiKeysMapper modelMapper = Mappers.getMapper(ApiKeysMapper.class);

    /**
     * Maps a ApiKeysResponse to a BffApiKeysResponse
     *
     * @param apiKeysResponse the ApiKeysResponse to map
     * @param paGroups        groups retrieved from selfcare and linked to current Public Administration and user
     * @return the mapped BffApiKeysResponse
     */
    public abstract BffApiKeysResponse mapApiKeysResponse(ApiKeysResponse apiKeysResponse, @Context List<PaGroup> paGroups);

    /**
     * Map api keys groups (String) to paGroups
     *
     * @param groups   api keys groups (String)
     * @param paGroups groups retrieved from selfcare and linked to current Public Administration and user
     * @return the list of groups where each element is made by id and name
     */
    protected List<BffApiKeyGroup> mapGroups(List<String> groups, @Context List<PaGroup> paGroups) {
        List<BffApiKeyGroup> apiKeyGroups = new ArrayList<>();
        for (String group : groups) {
            BffApiKeyGroup apiKeyGroup = new BffApiKeyGroup();
            // search api key group into pa groups
            PaGroup paGroup = paGroups.stream()
                    .filter(el -> el.getName() != null && el.getName().equals(group))
                    .findFirst()
                    .orElse(null);
            if (paGroup != null) {
                apiKeyGroup.setId(paGroup.getId());
                apiKeyGroup.setName(paGroup.getName());
            } else {
                apiKeyGroup.setId("no-id");
                apiKeyGroup.setName(group);
            }
            apiKeyGroups.add(apiKeyGroup);
        }
        return apiKeyGroups;
    }
}