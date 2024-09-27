package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestNewVirtualKey;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffNewVirtualKeyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffRequestNewVirtualKey to RequestNewVirtualKey
 */
@Mapper
public interface RequestNewVirtualKeysMapper {

    RequestNewVirtualKeysMapper modelMapper = Mappers.getMapper(RequestNewVirtualKeysMapper.class);

    /**
     * Maps a BffRequestNewVirtualKey to a RequestNewVirtualKey
     *
     * @param bffNewVirtualKeyRequest the BffRequestNewVirtualKey to map
     * @return the mapped RequestNewVirtualKey
     */
    RequestNewVirtualKey mapRequestNewVirtualKey(BffNewVirtualKeyRequest bffNewVirtualKeyRequest);
}