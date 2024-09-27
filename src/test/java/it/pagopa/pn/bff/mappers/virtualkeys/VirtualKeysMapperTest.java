package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.VirtualKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.*;
import it.pagopa.pn.bff.mappers.apikeys.ApiKeysMapper;
import it.pagopa.pn.bff.mocks.VirtualKeysMock;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class VirtualKeysMapperTest {
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

        BffVirtualKeysResponse bffVirtualKeysResponseNull = VirtualKeysMapper.modelMapper.mapVirtualKeysResponse(null);
        assertNull(bffVirtualKeysResponseNull);
    }
}
