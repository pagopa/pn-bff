package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestVirtualKeyStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffVirtualKeyStatusRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffRequestVirtualKeyStatus to RequestVirtualKeyStatus
 */
@Mapper
public interface RequestVirtualKeyStatusMapper {
    RequestVirtualKeyStatusMapper modelMapper = Mappers.getMapper(RequestVirtualKeyStatusMapper.class);

    /**
     * Maps a BffRequestVirtualKeyStatus to a RequestVirtualKeyStatus
     *
     * @param bffVirtualKeyStatusRequest the BffVirtualKeyStatusRequest to map
     * @return the mapped BffRequestVirtualKeyStatus
     */
    RequestVirtualKeyStatus mapRequestVirtualKeyStatus(BffVirtualKeyStatusRequest bffVirtualKeyStatusRequest);
}