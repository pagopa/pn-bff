package it.pagopa.pn.bff.mappers.tosprivacy;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TosPrivacyMapperTest {
    @Test
    void testConsentMapper() {
        Consent consent = new Consent();
        BffConsent actualMapConsentMapperResult = TosPrivacyMapper.tosPrivacyMapper.mapTosPrivacyConsent(consent);
        assertNotNull(actualMapConsentMapperResult);

        BffConsent mapConsentNull = TosPrivacyMapper.tosPrivacyMapper.mapTosPrivacyConsent(null);
        assertNull(mapConsentNull);
    }
}
