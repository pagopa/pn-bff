package it.pagopa.pn.bff.mappers.tosprivacy;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffConsent;
import it.pagopa.pn.bff.mocks.ConsentsMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TosPrivacyConsentMapperTest {
    private final ConsentsMock consentsMock = new ConsentsMock();

    @Test
    void testConsentMapper() {
        Consent consent = consentsMock.getTosConsentResponseMock();

        BffConsent actualMapConsentMapperResult = TosPrivacyConsentMapper.tosPrivacyConsentMapper.mapConsent(consent);
        assertNotNull(actualMapConsentMapperResult);
        assertEquals(actualMapConsentMapperResult.getConsentType().getValue(), consent.getConsentType().getValue());
        assertEquals(actualMapConsentMapperResult.getConsentVersion(), consent.getConsentVersion());
        assertEquals(actualMapConsentMapperResult.getAccepted(), consent.getAccepted());
        assertEquals(actualMapConsentMapperResult.getRecipientId(), consent.getRecipientId());
        assertEquals(actualMapConsentMapperResult.getIsFirstAccept(), consent.getIsFirstAccept());

        BffConsent mapConsentNull = TosPrivacyConsentMapper.tosPrivacyConsentMapper.mapConsent(null);
        assertNull(mapConsentNull);
    }
}