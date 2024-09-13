package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyRequest;
import it.pagopa.pn.bff.mocks.VirtualKeysMock;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestNewVirtualKeyMapperTest {

    private final VirtualKeysMock virtualKeysMock = new VirtualKeysMock();

    @Test
    void testRequestNewVirtualKeyMapper() {
        BffNewVirtualKeyRequest bffNewVirtualKeyRequest = new BffNewVirtualKeyRequest();
        bffNewVirtualKeyRequest.setName("mock-api-virtual-name");

        RequestNewVirtualKey requestNewVirtualKey = RequestNewVirtualKeysMapper.modelMapper.mapRequestNewVirtualKey(bffNewVirtualKeyRequest);
        assertNotNull(requestNewVirtualKey);
        assertThat(requestNewVirtualKey).usingRecursiveComparison().isEqualTo(virtualKeysMock.getRequestNewVirtualKeyMock());

        requestNewVirtualKey = RequestNewVirtualKeysMapper.modelMapper.mapRequestNewVirtualKey(null);
        assertNull(requestNewVirtualKey);
    }
}