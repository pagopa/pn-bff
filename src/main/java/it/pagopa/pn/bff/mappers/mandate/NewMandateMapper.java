package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.BffNewMandateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffNewMandateRequest to the MandateDto
 */
@Mapper
public interface NewMandateMapper {
    NewMandateMapper modelMapper = Mappers.getMapper(NewMandateMapper.class);

    /**
     * Maps a BffNewMandateRequest to a MandateDto
     *
     * @param request the BffNewMandateRequest to map
     * @return the mapped MandateDto
     */
    MandateDto mapRequest(BffNewMandateRequest request);
}