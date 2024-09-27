package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.ResponseNewVirtualKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffNewVirtualKeyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the ResponseNewVirtualKey to BffResponseNewVirtualKey
 */
@Mapper
public interface ResponseNewVirtualKeysMapper {

    ResponseNewVirtualKeysMapper modelMapper = Mappers.getMapper(ResponseNewVirtualKeysMapper.class);

    /**
     * Maps a ResponseNewVirtualKey to a BffNewVirtualKeyResponse
     *
     * @param responseNewVirtualKey the ResponseNewVirtualKey to map
     * @return the mapped ResponseNewVirtualKey
     */
    BffNewVirtualKeyResponse mapResponseNewVirtualKey(ResponseNewVirtualKey responseNewVirtualKey);
}
