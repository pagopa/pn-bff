package it.pagopa.pn.bff.mappers.virtualkeys;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeleteVirtualKeysMapper {
    DeleteVirtualKeysMapper modelMapper = Mappers.getMapper(DeleteVirtualKeysMapper.class);

}
