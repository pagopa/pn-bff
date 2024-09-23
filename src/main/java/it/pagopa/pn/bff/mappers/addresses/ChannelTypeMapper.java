package it.pagopa.pn.bff.mappers.addresses;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CourtesyChannelType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.LegalAndUnverifiedDigitalAddress;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.LegalChannelType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffChannelType;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffChannelType to the microservice ChannelType
 */
@Mapper
public interface ChannelTypeMapper {

    String SERCQ_SEND_VALUE = "x-pagopa-pn-sercq:SEND-self:notification-already-delivered";

    ChannelTypeMapper channelTypeMapper = Mappers.getMapper(ChannelTypeMapper.class);

    /**
     * Map BffChannelType to CourtesyChannelType
     *
     * @param channelType BffChannelType to map
     * @return CourtesyChannelType
     */
    @ValueMapping(source = "EMAIL", target = "EMAIL")
    @ValueMapping(source = "SMS", target = "SMS")
    @ValueMapping(source = "APPIO", target = "APPIO")
    @ValueMapping(source = "PEC", target = MappingConstants.NULL)
    @ValueMapping(source = "SERCQ_SEND", target = MappingConstants.NULL)
    CourtesyChannelType mapCourtesyChannelType(BffChannelType channelType);

    /**
     * Map BffChannelType to LegalChannelType
     *
     * @param channelType BffChannelType to map
     * @return LegalChannelType
     */
    @ValueMapping(source = "PEC", target = "PEC")
    @ValueMapping(source = "SERCQ_SEND", target = "SERCQ")
    @ValueMapping(source = "APPIO", target = MappingConstants.NULL)
    @ValueMapping(source = "SMS", target = MappingConstants.NULL)
    @ValueMapping(source = "EMAIL", target = MappingConstants.NULL)
    LegalChannelType mapLegalChannelType(BffChannelType channelType);

    /**
     * Map LegalChannelType to BffChannelType
     *
     * @param channelType LegalChannelType to map
     * @return BffChannelType
     */
    default BffChannelType mapLegalChannelType(LegalChannelType channelType, @Context LegalAndUnverifiedDigitalAddress address) {
        if (channelType.equals(LegalChannelType.PEC)) {
            return BffChannelType.PEC;
        } else if (channelType.equals(LegalChannelType.SERCQ) && SERCQ_SEND_VALUE.equals(address.getValue())) {
            return BffChannelType.SERCQ_SEND;
        }
        return null;
    }
}