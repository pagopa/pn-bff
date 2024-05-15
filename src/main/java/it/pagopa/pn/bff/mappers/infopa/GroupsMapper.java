package it.pagopa.pn.bff.mappers.infopa;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaGroup;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the ext-registry PaGroup to the bff PaGroup
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
    PaGroup mapGroups(it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup paGroup);
}