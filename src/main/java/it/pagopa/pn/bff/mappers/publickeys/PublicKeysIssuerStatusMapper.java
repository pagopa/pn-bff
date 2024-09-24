package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeysIssuerResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeysIssuerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the PublicKeysIssuerResponse
 * to the BffPublicKeysIssuerResponse
 */
@Mapper
public interface PublicKeysIssuerStatusMapper {
    PublicKeysIssuerStatusMapper modelMapper = Mappers.getMapper(PublicKeysIssuerStatusMapper.class);

    BffPublicKeysIssuerResponse mapPublicKeysIssuerStatus(PublicKeysIssuerResponse publicKeysIssuerResponse);
}