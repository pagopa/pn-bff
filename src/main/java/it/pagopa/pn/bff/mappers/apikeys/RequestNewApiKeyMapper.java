package it.pagopa.pn.bff.mappers.apikeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestNewApiKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffRequestNewApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the RequestNewApiKey to BffRequestNewApiKey
 */
@Mapper
public interface RequestNewApiKeyMapper {
    // Instance of the mapper
    RequestNewApiKeyMapper modelMapper = Mappers.getMapper(RequestNewApiKeyMapper.class);

    /**
     * Maps a BffRequestNewApiKey to a RequestNewApiKey
     *
     * @param bffRequestNewApiKey the BffRequestNewApiKey to map
     * @return the mapped RequestNewApiKey
     */
    RequestNewApiKey mapRequestNewApiKey(BffRequestNewApiKey bffRequestNewApiKey);
}