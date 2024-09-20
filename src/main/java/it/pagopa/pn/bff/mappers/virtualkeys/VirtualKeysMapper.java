package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.ApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.VirtualKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffApiKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffVirtualKeysResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VirtualKeysMapper {
    VirtualKeysMapper modelMapper = Mappers.getMapper(VirtualKeysMapper.class);

    /**
     * Maps a ApiKeysResponse to a BffApiKeysResponse
     *
     * @param virtualKeysResponse the ApiKeysResponse to map
     * @return the mapped BffVirtualKeysResponse
     */
    BffVirtualKeysResponse mapVirtualKeysResponse(VirtualKeysResponse virtualKeysResponse);
}
