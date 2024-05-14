package it.pagopa.pn.bff.mappers.infopa;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaGroup;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GroupsMapper {
    // Instance of the mapper
    GroupsMapper modelMapper = Mappers.getMapper(GroupsMapper.class);

    /**
     * Maps a PaGroup to it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup
     *
     * @param paGroup the PaGroup to map
     * @return the mapped it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup
     */
    PaGroup mapGroups(it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup paGroup);
}
