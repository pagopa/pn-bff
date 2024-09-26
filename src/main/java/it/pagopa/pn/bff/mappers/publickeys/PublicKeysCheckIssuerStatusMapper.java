package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeysIssuerResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeysCheckIssuerResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the PublicKeysIssuerResponse
 * to the bffPublicKeysCheckIssuerResponse
 */
@Mapper
public interface PublicKeysCheckIssuerStatusMapper {
    PublicKeysCheckIssuerStatusMapper modelMapper = Mappers.getMapper(PublicKeysCheckIssuerStatusMapper.class);

    /**
     * Maps a PublicKeysIssuerResponse to a BffPublicKeysCheckIssuerResponse
     *
     * @param publicKeysIssuerResponse the PublicKeysIssuerResponse to map
     * @param consent the Consent to map
     * @return the mapped BffPublicKeysCheckIssuerResponse
     */
    @Mapping(target = "issuer", source = "publicKeysIssuerResponse")
    @Mapping(target = "tosAccepted", source = "consent.accepted")
    BffPublicKeysCheckIssuerResponse mapPublicKeysCheckIssuerStatus(PublicKeysIssuerResponse publicKeysIssuerResponse, Consent consent);
//        BffPublicKeysCheckIssuerResponse bffPublicKeysCheckIssuerResponse = new BffPublicKeysCheckIssuerResponse();
//        bffPublicKeysCheckIssuerResponse.setIssuer(publicKeysIssuerResponse);
//        if(consent != null)
//            bffPublicKeysCheckIssuerResponse.setTosAccepted(consent.getAccepted());
//        else
//            bffPublicKeysCheckIssuerResponse.setTosAccepted(null);
//
//        return bffPublicKeysCheckIssuerResponse;
//    }
}