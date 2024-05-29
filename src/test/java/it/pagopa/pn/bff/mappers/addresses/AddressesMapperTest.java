package it.pagopa.pn.bff.mappers.addresses;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CourtesyDigitalAddress;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.LegalAndUnverifiedDigitalAddress;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.UserAddresses;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffUserAddress;
import it.pagopa.pn.bff.mocks.AddressesMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AddressesMapperTest {
    private final AddressesMock addressesMock = new AddressesMock();

    @Test
    void testMapUserAddresses() {
        UserAddresses userAddresses = addressesMock.getUserAddressesResponseMock();
        List<BffUserAddress> bffUserAddressList = AddressesMapper.addressesMapper.mapUserAddresses(userAddresses);
        assertNotNull(bffUserAddressList);
        assertEquals(bffUserAddressList.size(), userAddresses.getLegal().size() + userAddresses.getCourtesy().size());
    }

    @Test
    void testMapLegalAddress() {
        LegalAndUnverifiedDigitalAddress legalAddress = addressesMock.getUserAddressesResponseMock().getLegal().get(0);
        BffUserAddress bffUserAddress = AddressesMapper.addressesMapper.mapLegalAddress(legalAddress);
        assertNotNull(bffUserAddress);
        assertEquals(bffUserAddress.getAddressType(), legalAddress.getAddressType().name());
        assertEquals(bffUserAddress.getChannelType().getValue(), legalAddress.getChannelType().name());
        assertEquals(bffUserAddress.getValue(), legalAddress.getValue());
        assertEquals(bffUserAddress.getSenderId(), legalAddress.getSenderId());
    }

    @Test
    void testMapCourtesyAddress() {
        CourtesyDigitalAddress courtesyAddress = addressesMock.getUserAddressesResponseMock().getCourtesy().get(0);
        BffUserAddress bffUserAddress = AddressesMapper.addressesMapper.mapCourtesyAddress(courtesyAddress);
        assertNotNull(bffUserAddress);
        assertEquals(bffUserAddress.getAddressType(), courtesyAddress.getAddressType().name());
        assertEquals(bffUserAddress.getChannelType().getValue(), courtesyAddress.getChannelType().name());
        assertEquals(bffUserAddress.getValue(), courtesyAddress.getValue());
        assertEquals(bffUserAddress.getSenderId(), courtesyAddress.getSenderId());
    }
}
