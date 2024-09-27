package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the PublicKeyResponse to BffPublicKeyResponse
 */
@Mapper
public interface PublicKeyResponseMapper {
    // Instance of the mapper
    PublicKeyResponseMapper modelMapper = Mappers.getMapper(PublicKeyResponseMapper.class);

    /**
     * Maps a PublicKeyResponse to a BffPublicKeyResponse
     *
     * @param publicKeyResponse the ResponseNewApiKey to map
     * @return the mapped BffResponseNewApiKey
     */
    BffPublicKeyResponse mapPublicKeyResponse(PublicKeyResponse publicKeyResponse);
}