package it.pagopa.pn.bff.mappers.addresses;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.AddressVerification;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffAddressVerification;
import it.pagopa.pn.bff.mocks.AddressesMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AddressVerificationMapperTest {
    private final AddressesMock addressesMock = new AddressesMock();

    @Test
    void testAddressVerificationMapper() {
        BffAddressVerification addressVerification = addressesMock.getAddressVerificationMock();
        AddressVerification mappedAddressVerification = AddressVerificationMapper.addressVerificationMapper.mapAddressVerification(addressVerification);

        assertNotNull(mappedAddressVerification);
        assertEquals(addressVerification.getValue(), mappedAddressVerification.getValue());
        assertEquals(addressVerification.getVerificationCode(), mappedAddressVerification.getVerificationCode());
        assertEquals(addressVerification.getRequestId(), mappedAddressVerification.getRequestId());
    }

}
