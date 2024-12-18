package it.pagopa.pn.bff.mappers.infopa;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_private.model.AdditionalLanguages;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.BffAdditionalLanguages;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the AdditionalLanguages to the BffAdditionalLanguages
 */
@Mapper
public interface LanguageMapper {
    LanguageMapper modelMapper = Mappers.getMapper(LanguageMapper.class);

    /**
     * Maps an AdditionalLanguages to a BffAdditionalLanguages
     *
     * @param languages The AdditionalLanguages to map
     * @return The mapped BffAdditionalLanguages
     */
    BffAdditionalLanguages toBffAdditionalLanguages(AdditionalLanguages languages);


    @Mapping(target = "paId", source = "paId")
    AdditionalLanguages toAdditionalLanguages(String paId, BffAdditionalLanguages languages);
}
