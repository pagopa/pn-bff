package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeyRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeyRequest;
import it.pagopa.pn.bff.mocks.PublicKeysMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PublicKeyRequestMapperTest {
    private final PublicKeysMock publicKeysMock = new PublicKeysMock();

    @Test
    void testPublicKeyRequestMapper() {
        BffPublicKeyRequest bffPublicKeyRequest = new BffPublicKeyRequest();
        bffPublicKeyRequest.setName("mock-public-key-name");
        bffPublicKeyRequest.setPublicKey("mock-public-key-value");
        bffPublicKeyRequest.setAlgorithm(BffPublicKeyRequest.AlgorithmEnum.RS256);
        bffPublicKeyRequest.setExponent("mock-public-key-exponent");

        PublicKeyRequest publicKeyRequest = PublicKeyRequestMapper.modelMapper.mapPublicKeyRequest(bffPublicKeyRequest);
        assertNotNull(publicKeyRequest);
        assertThat(publicKeyRequest).usingRecursiveComparison().isEqualTo(publicKeysMock.gePublicKeyRequestMock());

        publicKeyRequest = PublicKeyRequestMapper.modelMapper.mapPublicKeyRequest(null);
        assertNull(publicKeyRequest);
    }
}