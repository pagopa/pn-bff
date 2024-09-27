package it.pagopa.pn.bff.mappers.addresses;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.AddressVerification;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.AddressVerificationResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffAddressVerificationRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffAddressVerificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffAddressVerificationRequest to the AddressVerification
 * and the AddressVerificationResponse to the BffAddressVerificationResponse
 */
@Mapper
public interface AddressVerificationMapper {
    AddressVerificationMapper addressVerificationMapper = Mappers.getMapper(AddressVerificationMapper.class);

    /**
     * Map BffAddressVerification to AddressVerification
     *
     * @param addressVerification BffAddressVerificationRequest to map
     * @return the mapped AddressVerification
     */
    AddressVerification mapAddressVerificationRequest(BffAddressVerificationRequest addressVerification);

    /**
     * Map AddressVerificationResponse to BffAddressVerificationResponse
     *
     * @param addressVerificationResponse AddressVerificationResponse to map
     * @return the mapped BffAddressVerificationResponse
     */
    BffAddressVerificationResponse mapAddressVerificationResponse(AddressVerificationResponse addressVerificationResponse);
}