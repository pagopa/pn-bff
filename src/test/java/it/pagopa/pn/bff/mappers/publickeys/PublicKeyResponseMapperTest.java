package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeyResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeyResponse;
import it.pagopa.pn.bff.mocks.PublicKeysMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PublicKeyResponseMapperTest {

    private final PublicKeysMock publicKeysMock = new PublicKeysMock();

    @Test
    void testPublicKeyResponseMapper() {
        PublicKeyResponse publicKeyResponse = publicKeysMock.gePublicKeyResponseMock();
        BffPublicKeyResponse bffPublicKeyResponse = PublicKeyResponseMapper.modelMapper.mapPublicKeyResponse(publicKeyResponse);
        assertNotNull(bffPublicKeyResponse);
        assertThat(bffPublicKeyResponse).usingRecursiveComparison().isEqualTo(publicKeyResponse);

        bffPublicKeyResponse = PublicKeyResponseMapper.modelMapper.mapPublicKeyResponse(null);
        assertNull(bffPublicKeyResponse);
    }
}
