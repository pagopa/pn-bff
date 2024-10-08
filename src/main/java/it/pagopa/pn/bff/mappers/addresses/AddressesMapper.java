package it.pagopa.pn.bff.mappers.addresses;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CourtesyDigitalAddress;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.LegalAndUnverifiedDigitalAddress;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.UserAddresses;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffUserAddress;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapstruct mapper interface, used to map the UserAddresses (including
 * LegalAndUnverifiedDigitalAddress and CourtesyDigitalAddress) to
 * the BffUserAddress
 */
@Mapper(uses = {ChannelTypeMapper.class})
public interface AddressesMapper {
    AddressesMapper addressesMapper = Mappers.getMapper(AddressesMapper.class);

    /**
     * Map UserAddresses to BffUserAddress
     *
     * @param userAddresses UserAddresses
     * @return List of BffUserAddress
     */
    default List<BffUserAddress> mapUserAddresses(UserAddresses userAddresses) {
        List<LegalAndUnverifiedDigitalAddress> legal = userAddresses.getLegal();
        List<CourtesyDigitalAddress> courtesy = userAddresses.getCourtesy();

        List<BffUserAddress> bffUserAddress = new ArrayList<>();

        if (legal != null) {
            for (LegalAndUnverifiedDigitalAddress legalAddress : legal) {
                bffUserAddress.add(mapLegalAddress(legalAddress, legalAddress));
            }
        }

        if (courtesy != null) {
            for (CourtesyDigitalAddress courtesyAddress : courtesy) {
                bffUserAddress.add(mapCourtesyAddress(courtesyAddress));
            }
        }

        return bffUserAddress;
    }

    /**
     * Map LegalAndUnverifiedDigitalAddress to BffUserAddress
     *
     * @param legalAddress LegalAndUnverifiedDigitalAddress to map
     * @return the mapped BffUserAddress
     */
    BffUserAddress mapLegalAddress(LegalAndUnverifiedDigitalAddress legalAddress, @Context LegalAndUnverifiedDigitalAddress address);

    /**
     * Map CourtesyDigitalAddress to BffUserAddress
     *
     * @param courtesyAddress CourtesyDigitalAddress to map
     * @return the mapped BffUserAddress
     */
    BffUserAddress mapCourtesyAddress(CourtesyDigitalAddress courtesyAddress);
}