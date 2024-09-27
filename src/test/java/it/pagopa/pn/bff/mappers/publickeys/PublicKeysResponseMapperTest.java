package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeyRow;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeysResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeysResponse;
import it.pagopa.pn.bff.mocks.PublicKeysMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PublicKeysResponseMapperTest {

    private final PublicKeysMock publicKeysMock = new PublicKeysMock();

    @Test
    void testPublicKeysResponseMapper() {
        PublicKeysResponse publicKeysResponse = publicKeysMock.getPublicKeysMock();

        BffPublicKeysResponse bffPublicKeysResponse = PublicKeysResponseMapper.modelMapper.mapPublicKeysResponse(publicKeysResponse);
        assertNotNull(bffPublicKeysResponse);
        assertEquals(bffPublicKeysResponse.getLastKey(), publicKeysResponse.getLastKey());
        assertEquals(bffPublicKeysResponse.getCreatedAt(), publicKeysResponse.getCreatedAt());
        assertEquals(bffPublicKeysResponse.getTotal(), publicKeysResponse.getTotal());
        assertEquals(bffPublicKeysResponse.getItems().size(), publicKeysResponse.getItems().size());
        // check on single public key
        for(int i = 0; i < bffPublicKeysResponse.getItems().size(); i++) {
            PublicKeyRow publicKeyRow = publicKeysResponse.getItems().get(i);
            it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.PublicKeyRow bffPublicKeyRow = bffPublicKeysResponse.getItems().get(i);
            assertEquals(bffPublicKeyRow.getKid(), publicKeyRow.getKid());
            assertEquals(bffPublicKeyRow.getName(), publicKeyRow.getName());
            assertEquals(bffPublicKeyRow.getValue(), publicKeyRow.getValue());
            assertNotNull(publicKeyRow.getStatus());
            assertEquals(bffPublicKeyRow.getStatus().getValue(), publicKeyRow.getStatus().getValue());
            assertEquals(bffPublicKeyRow.getCreatedAt(), publicKeyRow.getCreatedAt());
            assertEquals(bffPublicKeyRow.getIssuer(), publicKeyRow.getIssuer());
            assertThat(bffPublicKeyRow.getStatusHistory()).usingRecursiveComparison().isEqualTo(publicKeyRow.getStatusHistory());
        }

        BffPublicKeysResponse bffPublicKeysResponseNull = PublicKeysResponseMapper.modelMapper.mapPublicKeysResponse(null);
        assertNull(bffPublicKeysResponseNull);
    }
}