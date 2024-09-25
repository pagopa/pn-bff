package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeysIssuerResponse;
import it.pagopa.pn.bff.mocks.PublicKeysMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PublicKeysIssuerStatusMapperTest {
    private final PublicKeysMock publicKeysMock = new PublicKeysMock();

    @Test
    void PublicKeysIssuerStatusMapper() {
        PublicKeysIssuerResponse publicKeysIssuerResponse = new PublicKeysIssuerResponse();
        publicKeysIssuerResponse.setIssuerStatus(PublicKeysIssuerResponse.IssuerStatusEnum.ACTIVE);
        publicKeysIssuerResponse.setIsPresent(true);

        it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.PublicKeysIssuerResponse mappedPublicKeysIssuerResponse =
                PublicKeysIssuerStatusMapper.modelMapper.mapPublicKeysIssuerStatus(publicKeysIssuerResponse);
        assertNotNull(mappedPublicKeysIssuerResponse);
        assertThat(mappedPublicKeysIssuerResponse).usingRecursiveComparison().isEqualTo(publicKeysMock.getIssuerStatusPublicKeysResponseMock());

        mappedPublicKeysIssuerResponse = PublicKeysIssuerStatusMapper.modelMapper.mapPublicKeysIssuerStatus(null);
        assertNull(mappedPublicKeysIssuerResponse);
    }
}
