package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.SearchMandateRequestDto;
import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.SearchMandateResponseDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSearchMandateRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSearchMandateResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.MandateDto;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

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
    @Mapping(source = "resultsPage", target = "resultsPage", qualifiedByName = "mapResultsPage")
    BffSearchMandateResponse mapResponse(SearchMandateResponseDto response);

    /**
     * Map result array
     *
     * @param result the result array
     * @return the mapped result array
     */
    @Named("mapResultsPage")
    @IterableMapping(qualifiedByName = "mapResultPage")
    List<MandateDto> mapResultsPage(List<it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateDto> result);

    /**
     * Map mandate
     *
     * @param mandate mandate
     * @return the mapped mandate
     */
    @Named("mapResultPage")
    @Mapping(target = "delegate", ignore = true)
    MandateDto mapResultPage(it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateDto mandate);
}