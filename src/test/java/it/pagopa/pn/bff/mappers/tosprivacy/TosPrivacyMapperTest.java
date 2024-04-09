package it.pagopa.pn.bff.mappers.tosprivacy;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
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

    @Test
    void testConvertConsentAction() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyActionBody.ActionEnum acceptAction = it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyActionBody.ActionEnum.ACCEPT;
        ConsentAction.ActionEnum convertAcceptAction = TosPrivacyMapper.tosPrivacyMapper.convertConsentAction(acceptAction);
        assertNotNull(convertAcceptAction);
        assert (convertAcceptAction == ConsentAction.ActionEnum.ACCEPT);

        it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyActionBody.ActionEnum declineAction = it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyActionBody.ActionEnum.DECLINE;
        ConsentAction.ActionEnum convertDeclineAction = TosPrivacyMapper.tosPrivacyMapper.convertConsentAction(declineAction);
        assertNotNull(convertDeclineAction);
        assert (convertDeclineAction == ConsentAction.ActionEnum.DECLINE);

        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction.ActionEnum convertConsentActionNull = TosPrivacyMapper.tosPrivacyMapper.convertConsentAction(null);
        assertNull(convertConsentActionNull);
    }
}
