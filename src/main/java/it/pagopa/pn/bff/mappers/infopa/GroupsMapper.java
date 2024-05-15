package it.pagopa.pn.bff.mappers.infopa;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPaGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPaGroupStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the ext-registry PaGroup to the bff PaGroup,
 * and the bff PaGroupStatus to the ext-registry PaGroupStatus
 */
@Mapper
public interface GroupsMapper {
    // Instance of the mapper
    GroupsMapper modelMapper = Mappers.getMapper(GroupsMapper.class);

    /**
     * Maps an ext-registry PaGroup to bff PaGroup
     *
     * @param paGroup the ext-registry PaGroup to map
     * @return the mapped bff PaGroup
     */
    BffPaGroup mapGroups(PaGroup paGroup);

    /**
     * Maps a bff PaGroupStatus to an ext-registry PaGroupStatus
     *
     * @param bffPaGroupStatus the bff PaGroupStatus to map
     * @return the mapped ext-registry PaGroupStatus
     */
    PaGroupStatus mapGroupStatus(BffPaGroupStatus bffPaGroupStatus);
}