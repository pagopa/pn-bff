package it.pagopa.pn.bff.mappers.virtualkeys;

import it.pagopa.pn.bff.generated.openapi.msclient.virtualkey_pg.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNewVirtualKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffResponseNewApiKey;
import it.pagopa.pn.bff.mocks.VirtualKeysMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResponseNewVirtualKeyMapperTest {

    private final VirtualKeysMock virtualKeysMock = new VirtualKeysMock();

    @Test
    void testResponseNewVirtualKeyMapper() {
        ResponseNewVirtualKey responseNewVirtualKey = virtualKeysMock.getResponseNewVirtualKeyMock();
        BffNewVirtualKeyResponse bffNewVirtualKeyResponse = ResponseNewVirtualKeysMapper.modelMapper.mapResponseNewVirtualKey(responseNewVirtualKey);
        assertNotNull(bffNewVirtualKeyResponse);
        assertThat(bffNewVirtualKeyResponse).usingRecursiveComparison().isEqualTo(responseNewVirtualKey);

        bffNewVirtualKeyResponse = ResponseNewVirtualKeysMapper.modelMapper.mapResponseNewVirtualKey(null);
        assertNull(bffNewVirtualKeyResponse);
    }
}