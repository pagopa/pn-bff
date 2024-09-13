package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.ResponseNewVirtualKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ResponseNewVirtualKeysMapper {

    ResponseNewVirtualKeysMapper modelMapper = Mappers.getMapper(ResponseNewVirtualKeysMapper.class);

    /**
     * Maps a BffRequestNewVirtualKey to a RequestNewVirtualKey
     *
     * @param bffNewVirtualKeyResponse the BffRequestNewVirtualKey to map
     * @return the mapped RequestNewApiKey
     */
    BffNewVirtualKeyResponse mapResponseNewVirtualKey(ResponseNewVirtualKey bffNewVirtualKeyResponse);
}
