package it.pagopa.pn.bff.mappers.apikeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestApiKeyStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffRequestApiKeyStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffRequestApiKeyStatus to RequestApiKeyStatus
 */
@Mapper
public interface RequestApiKeyStatusMapper {
    // Instance of the mapper
    RequestApiKeyStatusMapper modelMapper = Mappers.getMapper(RequestApiKeyStatusMapper.class);

    /**
     * Maps a BffRequestApiKeyStatus to a RequestApiKeyStatus
     *
     * @param bffRequestApiKeyStatus the BffRequestApiKeyStatus to map
     * @return the mapped RequestApiKeyStatus
     */
    RequestApiKeyStatus mapRequestApiKeyStatus(BffRequestApiKeyStatus bffRequestApiKeyStatus);
}