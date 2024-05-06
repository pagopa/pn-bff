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

    AddressVerification mapAddressVerification(BffAddressVerification addressVerification);

    BffAddressVerificationResponse mapAddressVerificationResponse(AddressVerificationResponse addressVerificationResponse);
}
