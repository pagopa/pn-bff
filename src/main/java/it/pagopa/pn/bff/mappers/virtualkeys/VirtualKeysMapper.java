package it.pagopa.pn.bff.mappers.virtualkeys;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VirtualKeysMapper {
    VirtualKeysMapper modelMapper = Mappers.getMapper(VirtualKeysMapper.class);
}
