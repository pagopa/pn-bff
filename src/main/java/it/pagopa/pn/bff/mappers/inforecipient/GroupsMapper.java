package it.pagopa.pn.bff.mappers.inforecipient;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroup;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroupStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.BffPgGroup;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.BffPgGroupStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * Mapstruct mapper interface, used to map the ext-registry PgGroupStatus to the bff PgGroupStatus,
 * and the ext-registry PgGroup to the bff PgGroup
 */
@Mapper
public interface GroupsMapper {
    // Instance of the mapper
    GroupsMapper modelMapper = Mappers.getMapper(GroupsMapper.class);


    /**
     * Method to map the PgGroup to the BffPgGroup
     *
     * @param pgGroup PgGroup
     * @return BffPgGroup
     */
    BffPgGroup mapGroups(PgGroup pgGroup);

    /**
     * Method to map the BffPgGroupStatus to the PgGroupStatus
     *
     * @param status BffPgGroupStatus
     * @return PgGroupStatus
     */
    PgGroupStatus mapGroupStatus(BffPgGroupStatus status);

}