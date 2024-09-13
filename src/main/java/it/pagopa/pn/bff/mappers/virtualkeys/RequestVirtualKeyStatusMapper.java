package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestVirtualKeyStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffVirtualKeyStatusRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestVirtualKeyStatusMapper {
    RequestVirtualKeyStatusMapper modelmapper = Mappers.getMapper(RequestVirtualKeyStatusMapper.class);

    /**
     * Maps a BffRequestVirtualKeyStatus to a RequestVirtualKeyStatus
     *
     * @param bffVirtualKeyStatusRequest the BffVirtualKeyStatusRequest to map
     * @return the mapped RequestVirtualKeyStatus
     */
    RequestVirtualKeyStatus mapRequestVirtualKeyStatus(BffVirtualKeyStatusRequest bffVirtualKeyStatusRequest);
}
