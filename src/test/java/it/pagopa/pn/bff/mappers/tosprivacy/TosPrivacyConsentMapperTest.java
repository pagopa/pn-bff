package it.pagopa.pn.bff.mappers.tosprivacy;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.ConsentType;
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

    @Test
    void testConsentActionMapper() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody.ActionEnum
                acceptAction = it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody.ActionEnum.ACCEPT;
        ConsentAction.ActionEnum convertAcceptAction = TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentAction(acceptAction);
        assertNotNull(convertAcceptAction);
        assertEquals(convertAcceptAction, ConsentAction.ActionEnum.ACCEPT);

        it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody.ActionEnum
                declineAction = it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody.ActionEnum.DECLINE;
        ConsentAction.ActionEnum convertDeclineAction = TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentAction(declineAction);
        assertNotNull(convertDeclineAction);
        assertEquals(convertDeclineAction, ConsentAction.ActionEnum.DECLINE);

        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction.ActionEnum
                convertConsentActionNull = TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentAction(null);
        assertNull(convertConsentActionNull);
    }

    @Test
    void testConsentTypeMapper() {
        ConsentType consentType = ConsentType.TOS;
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType convertConsentType = TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentType(consentType);
        assertNotNull(convertConsentType);
        assertEquals(convertConsentType.getValue(), ConsentType.TOS.getValue());


        consentType = ConsentType.DATAPRIVACY;
        convertConsentType = TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentType(consentType);
        assertNotNull(convertConsentType);
        assertEquals(convertConsentType.getValue(), ConsentType.DATAPRIVACY.getValue());

        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType consentTypeNull = TosPrivacyConsentMapper.tosPrivacyConsentMapper.convertConsentType(null);
        assertNull(consentTypeNull);
    }
}