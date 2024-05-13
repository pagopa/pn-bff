package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.AcceptRequestDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffAcceptRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffAcceptRequest to the AcceptRequestDto
 */
@Mapper
public interface AcceptMandateMapper {
    AcceptMandateMapper modelMapper = Mappers.getMapper(AcceptMandateMapper.class);

    /**
     * Maps a BffAcceptRequest to a AcceptRequestDto
     *
     * @param request the BffAcceptRequest to map
     * @return the mapped AcceptRequestDto
     */
    AcceptRequestDto mapRequest(BffAcceptRequest request);
}