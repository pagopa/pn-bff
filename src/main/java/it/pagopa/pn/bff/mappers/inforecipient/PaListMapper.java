package it.pagopa.pn.bff.mappers.inforecipient;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaSummary;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the ext-registry PaSummary to the bff BffPaSummary
 */
@Mapper
public interface PaListMapper {
    // Instance of the mapper
    PaListMapper modelMapper = Mappers.getMapper(PaListMapper.class);

    /**
     * Method to map the PaSummary to the BffPaSummary
     *
     * @param paList List of PA
     * @return BffPaSummary
     */
    it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.PaSummary mapPaList(PaSummary paList);
}