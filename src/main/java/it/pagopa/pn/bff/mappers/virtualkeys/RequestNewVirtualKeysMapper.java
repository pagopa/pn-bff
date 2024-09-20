package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestNewVirtualKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffNewVirtualKeyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestNewVirtualKeysMapper {

    RequestNewVirtualKeysMapper modelMapper = Mappers.getMapper(RequestNewVirtualKeysMapper.class);

    /**
     * Maps a BffRequestNewApiKey to a RequestNewApiKey
     *
     * @param bffNewVirtualKeyRequest the BffRequestNewApiKey to map
     * @return the mapped RequestNewApiKey
     */
    RequestNewVirtualKey mapRequestNewVirtualKey(BffNewVirtualKeyRequest bffNewVirtualKeyRequest);
}
