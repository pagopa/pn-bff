package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateCountsDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.BffMandatesCount;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the MandateCountsDto to the BffMandatesCount
 */
@Mapper
public interface MandateCountMapper {
    MandateCountMapper modelMapper = Mappers.getMapper(MandateCountMapper.class);

    /**
     * Maps a MandateCountsDto to a BffMandatesCount
     *
     * @param count the MandateCountsDto to map
     * @return the mapped BffMandatesCount
     */
    BffMandatesCount mapCount(MandateCountsDto count);
}