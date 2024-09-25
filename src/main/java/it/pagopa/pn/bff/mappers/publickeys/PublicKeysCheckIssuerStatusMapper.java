package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeysCheckIssuerResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.PublicKeysIssuerResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the PublicKeysIssuerResponse
 * to the BffPublicKeysIssuerResponse
 */
@Mapper
public interface PublicKeysCheckIssuerStatusMapper {
    PublicKeysCheckIssuerStatusMapper modelMapper = Mappers.getMapper(PublicKeysCheckIssuerStatusMapper.class);

    default BffPublicKeysCheckIssuerResponse mapPublicKeysIssuerStatus(PublicKeysIssuerResponse publicKeysIssuerResponse, @Context Consent tosAccepted) {
        BffPublicKeysCheckIssuerResponse bffPublicKeysCheckIssuerResponse = new BffPublicKeysCheckIssuerResponse();
        bffPublicKeysCheckIssuerResponse.setIssuer(publicKeysIssuerResponse);
        bffPublicKeysCheckIssuerResponse.setTosAccepted(tosAccepted.getAccepted());

        return bffPublicKeysCheckIssuerResponse;
    }
}