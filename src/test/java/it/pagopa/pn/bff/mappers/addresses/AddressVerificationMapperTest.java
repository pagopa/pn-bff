package it.pagopa.pn.bff.mappers.addresses;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.AddressVerification;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffAddressVerificationRequest;
import it.pagopa.pn.bff.mocks.AddressesMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AddressVerificationMapperTest {
    private final AddressesMock addressesMock = new AddressesMock();

    @Test
    void testAddressVerificationMapper() {
        BffAddressVerificationRequest addressVerification = addressesMock.getBffAddressVerificationMock();
        AddressVerification mappedAddressVerification = AddressVerificationMapper
                .addressVerificationMapper
                .mapAddressVerificationRequest(addressVerification);

        assertNotNull(mappedAddressVerification);
        assertEquals(addressVerification.getValue(), mappedAddressVerification.getValue());
        assertEquals(addressVerification.getVerificationCode(), mappedAddressVerification.getVerificationCode());
        assertEquals(addressVerification.getRequestId(), mappedAddressVerification.getRequestId());
    }

}
