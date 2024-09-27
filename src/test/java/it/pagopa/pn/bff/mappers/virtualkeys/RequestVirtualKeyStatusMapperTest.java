package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestVirtualKeyStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffVirtualKeyStatusRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestVirtualKeyStatusMapperTest {
    @Test
    void testRequestVirtualKeyStatusMapper() {
        BffVirtualKeyStatusRequest bffVirtualKeyStatusRequest = new BffVirtualKeyStatusRequest();
        bffVirtualKeyStatusRequest.setStatus(BffVirtualKeyStatusRequest.StatusEnum.ROTATE);
        RequestVirtualKeyStatus requestVirtualKeyStatus = RequestVirtualKeyStatusMapper.modelmapper.mapRequestVirtualKeyStatus(bffVirtualKeyStatusRequest);
        assertNotNull(requestVirtualKeyStatus);
        assertThat(requestVirtualKeyStatus).usingRecursiveComparison().isEqualTo(bffVirtualKeyStatusRequest);

        requestVirtualKeyStatus = RequestVirtualKeyStatusMapper.modelmapper.mapRequestVirtualKeyStatus(null);
        assertNull(requestVirtualKeyStatus);
    }

}
