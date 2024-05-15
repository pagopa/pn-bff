package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffMandate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the MandateDto to the BffMandate
 */
@Mapper
public interface MandatesByDelegateMapper {
    MandatesByDelegateMapper modelMapper = Mappers.getMapper(MandatesByDelegateMapper.class);

    /**
     * Maps a MandateDto to a BffMandate
     *
     * @param mandate the MandateDto to map
     * @return the mapped BffMandate
     */
    BffMandate mapMandate(MandateDto mandate);
}