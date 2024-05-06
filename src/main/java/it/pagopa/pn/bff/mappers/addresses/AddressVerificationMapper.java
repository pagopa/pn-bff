package it.pagopa.pn.bff.mappers.addresses;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.AddressVerification;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.AddressVerificationResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffAddressVerification;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffAddressVerificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressVerificationMapper {
    AddressVerificationMapper addressVerificationMapper = Mappers.getMapper(AddressVerificationMapper.class);

    /**
     * Map BffAddressVerification to AddressVerification
     *
     * @param addressVerification BffAddressVerification to map
     * @return the mapped AddressVerification
     */
    AddressVerification mapAddressVerification(BffAddressVerification addressVerification);

    /**
     * Map AddressVerificationResponse to BffAddressVerificationResponse
     *
     * @param addressVerificationResponse AddressVerificationResponse to map
     * @return the mapped BffAddressVerificationResponse
     */
    BffAddressVerificationResponse mapAddressVerificationResponse(AddressVerificationResponse addressVerificationResponse);
}
