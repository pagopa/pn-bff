package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.VirtualKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffVirtualKeysResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the VirtualKeysResponse
 * to the BffVirtualKeysResponse
 */
@Mapper
public interface VirtualKeysMapper {
    VirtualKeysMapper modelMapper = Mappers.getMapper(VirtualKeysMapper.class);

    /**
     * Maps a VirtualKeysResponse to a BffVirtualKeysResponse
     *
     * @param virtualKeysResponse the VirtualKeysResponse to map
     * @return the mapped BffVirtualKeysResponse
     */
    BffVirtualKeysResponse mapVirtualKeysResponse(VirtualKeysResponse virtualKeysResponse);
}
