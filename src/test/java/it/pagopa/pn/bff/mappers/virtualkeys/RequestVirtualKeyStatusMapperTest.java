package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.RequestApiKeyStatus;
import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.RequestVirtualKeyStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffRequestApiKeyStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffVirtualKeyStatusRequest;
import it.pagopa.pn.bff.mappers.apikeys.RequestApiKeyStatusMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RequestVirtualKeyStatusMapperTest {
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
