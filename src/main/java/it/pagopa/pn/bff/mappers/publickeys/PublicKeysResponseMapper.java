package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeysResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the PublicKeysResponse
 * to the BffPublicKeysResponse
 */
@Mapper
public interface PublicKeysResponseMapper {
    PublicKeysResponseMapper modelMapper = Mappers.getMapper(PublicKeysResponseMapper.class);

    /**
     * Maps a PublicKeysResponse to a BffPublicKeysResponse
     *
     * @param publicKeysResponse the PublicKeysResponse to map
     * @return the mapped BffPublicKeysResponse
     */
    BffPublicKeysResponse mapPublicKeysResponse(PublicKeysResponse publicKeysResponse);
}