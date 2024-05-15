package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.SearchMandateRequestDto;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.SearchMandateResponseDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSearchMandateRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSearchMandateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffSearchMandateRequest to the SearchMandateRequestDto, and
 * the SearchMandateResponseDto to the BffSearchMandateResponse
 */
@Mapper
public interface SearchMandateByDelegateMapper {
    SearchMandateByDelegateMapper modelMapper = Mappers.getMapper(SearchMandateByDelegateMapper.class);

    /**
     * Maps a BffSearchMandateRequest to a SearchMandateRequestDto
     *
     * @param request the BffSearchMandateRequest to map
     * @return the mapped SearchMandateRequestDto
     */
    SearchMandateRequestDto mapRequest(BffSearchMandateRequest request);

    /**
     * Maps a SearchMandateResponseDto to a BffSearchMandateResponse
     *
     * @param response the SearchMandateResponseDto to map
     * @return the mapped BffSearchMandateResponse
     */
    BffSearchMandateResponse mapResponse(SearchMandateResponseDto response);
}