package it.pagopa.pn.bff.mappers.apikeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ResponseNewApiKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffResponseNewApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the ResponseNewApiKey to BffResponseNewApiKey
 */
@Mapper
public interface ResponseNewApiKeyMapper {
    // Instance of the mapper
    ResponseNewApiKeyMapper modelMapper = Mappers.getMapper(ResponseNewApiKeyMapper.class);

    /**
     * Maps a ResponseNewApiKey to a BffResponseNewApiKey
     *
     * @param responseNewApiKey the ResponseNewApiKey to map
     * @return the mapped BffResponseNewApiKey
     */
    BffResponseNewApiKey mapResponseNewApiKey(ResponseNewApiKey responseNewApiKey);
}