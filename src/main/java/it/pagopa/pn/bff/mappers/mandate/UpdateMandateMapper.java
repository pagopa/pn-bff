package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.UpdateRequestDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.BffUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffUpdateRequest to the UpdateRequestDto
 */
@Mapper
public interface UpdateMandateMapper {
    UpdateMandateMapper modelMapper = Mappers.getMapper(UpdateMandateMapper.class);

    /**
     * Maps a BffUpdateRequest to a UpdateRequestDto
     *
     * @param request the BffUpdateRequest to map
     * @return the mapped UpdateRequestDto
     */
    UpdateRequestDto mapRequest(BffUpdateRequest request);
}