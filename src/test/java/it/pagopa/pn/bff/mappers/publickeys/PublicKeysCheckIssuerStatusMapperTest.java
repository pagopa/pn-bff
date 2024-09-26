package it.pagopa.pn.bff.mappers.publickeys;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.BffPublicKeysCheckIssuerResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.PublicKeysIssuerResponse;
import it.pagopa.pn.bff.mocks.ConsentsMock;
import it.pagopa.pn.bff.mocks.PublicKeysMock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PublicKeysCheckIssuerStatusMapperTest {
    private final PublicKeysMock publicKeysMock = new PublicKeysMock();
    private final ConsentsMock consentsMock = new ConsentsMock();

    @Test
    void testPublicKeysCheckIssuerStatusMapper() {
        PublicKeysIssuerResponse publicKeysIssuerResponse = new PublicKeysIssuerResponse();
        Consent consent;

        publicKeysIssuerResponse.setIssuerStatus(PublicKeysIssuerResponse.IssuerStatusEnum.ACTIVE);
        publicKeysIssuerResponse.setIsPresent(true);

        consent = consentsMock.getB2BConsentResponseMock();

        BffPublicKeysCheckIssuerResponse bffPublicKeysCheckIssuerResponse =
                PublicKeysCheckIssuerStatusMapper.modelMapper.mapPublicKeysCheckIssuerStatus(publicKeysIssuerResponse, consent);

        assertNotNull(bffPublicKeysCheckIssuerResponse);
        assertThat(bffPublicKeysCheckIssuerResponse).usingRecursiveComparison().isEqualTo(publicKeysMock.checkIssuerStatusPublicKeysResponseMock());

        bffPublicKeysCheckIssuerResponse = PublicKeysCheckIssuerStatusMapper.modelMapper.mapPublicKeysCheckIssuerStatus(null, null);
        assertNull(bffPublicKeysCheckIssuerResponse);
    }
}
