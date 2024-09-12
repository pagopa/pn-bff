package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPublicKeyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
* Mapstruct mapper interface, used to map the PublicKeyRequest
* to the BffPublicKeyRequest
*/
@Mapper
public interface PublicKeyRequestMapper {
   PublicKeyRequestMapper modelMapper = Mappers.getMapper(PublicKeyRequestMapper.class);

   /**
    * Maps a BffPublicKeyRequest to a PublicKeyRequest
    *
    * @param bffPublicKeyRequest the BffPublicKeyRequest to map
    * @return the mapped PublicKeyRequest
    */
   PublicKeyRequest mapPublicKeyRequest(BffPublicKeyRequest bffPublicKeyRequest);
}