package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.VirtualKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.*;
import it.pagopa.pn.bff.mocks.VirtualKeysMock;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VirtualKeysMapperTest {
    private final VirtualKeysMock virtualKeysMock = new VirtualKeysMock();

    @Test
    void testVirtualKeysMapper() {
        VirtualKeysResponse virtualKeysResponse = virtualKeysMock.getVirtualKeysMock();

        BffVirtualKeysResponse bffVirtualKeysResponse = VirtualKeysMapper.modelMapper.mapVirtualKeysResponse(virtualKeysResponse);
        assertNotNull(bffVirtualKeysResponse);
        assertEquals(bffVirtualKeysResponse.getLastKey(), virtualKeysResponse.getLastKey());
        assertEquals(bffVirtualKeysResponse.getLastUpdate(), virtualKeysResponse.getLastUpdate());
        assertEquals(bffVirtualKeysResponse.getTotal(), virtualKeysResponse.getTotal());
        assertEquals(bffVirtualKeysResponse.getItems().size(), virtualKeysResponse.getItems().size());

        for (int i = 0; i < bffVirtualKeysResponse.getItems().size(); i++) {
            it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.VirtualKey virtualKeyRow = virtualKeysResponse.getItems().get(i);
            VirtualKey virtualKeyResponseRow = bffVirtualKeysResponse.getItems().get(i);
            assertEquals(virtualKeyResponseRow.getId(), virtualKeyRow.getId());
            assertEquals(virtualKeyResponseRow.getName(), virtualKeyRow.getName());
            assertEquals(virtualKeyResponseRow.getValue(), virtualKeyRow.getValue());
            assertNotNull(virtualKeyRow.getStatus());
            assertEquals(virtualKeyResponseRow.getStatus().getValue(), virtualKeyRow.getStatus().getValue());
            assertEquals(virtualKeyResponseRow.getLastUpdate(), virtualKeyRow.getLastUpdate());
        }

        BffVirtualKeysResponse bffVirtualKeysResponseNull = VirtualKeysMapper.modelMapper.mapVirtualKeysResponse(null);
        assertNull(bffVirtualKeysResponseNull);
    }
}
