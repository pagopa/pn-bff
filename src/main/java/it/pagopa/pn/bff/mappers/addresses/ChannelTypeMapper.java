package it.pagopa.pn.bff.mappers.addresses;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CourtesyChannelType;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.LegalChannelType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffChannelType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChannelTypeMapper {
    ChannelTypeMapper channelTypeMapper = Mappers.getMapper(ChannelTypeMapper.class);

    @ValueMapping(source = "EMAIL", target = "EMAIL")
    @ValueMapping(source = "SMS", target = "SMS")
    @ValueMapping(source = "APPIO", target = "APPIO")
    @ValueMapping(source = "PEC", target = MappingConstants.NULL)
    CourtesyChannelType mapCourtesyChannelType(BffChannelType channelType);

    @ValueMapping(source = "PEC", target = "PEC")
    @ValueMapping(source = "APPIO", target = "APPIO")
    @ValueMapping(source = "SMS", target = MappingConstants.NULL)
    @ValueMapping(source = "EMAIL", target = MappingConstants.NULL)
    LegalChannelType mapLegalChannelType(BffChannelType channelType);
}
