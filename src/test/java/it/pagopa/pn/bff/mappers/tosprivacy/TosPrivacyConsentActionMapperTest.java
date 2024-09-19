package it.pagopa.pn.bff.mappers.tosprivacy;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TosPrivacyConsentActionMapperTest {
    @Test
    void testConsentActionMapper() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyActionBody.ActionEnum
                acceptAction = it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyActionBody.ActionEnum.ACCEPT;
        ConsentAction.ActionEnum convertAcceptAction = TosPrivacyConsentActionMapper.tosPrivacyConsentActionMapper.convertConsentAction(acceptAction);
        assertNotNull(convertAcceptAction);
        assertEquals(convertAcceptAction, ConsentAction.ActionEnum.ACCEPT);

        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyActionBody.ActionEnum
                declineAction = it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.BffTosPrivacyActionBody.ActionEnum.DECLINE;
        ConsentAction.ActionEnum convertDeclineAction = TosPrivacyConsentActionMapper.tosPrivacyConsentActionMapper.convertConsentAction(declineAction);
        assertNotNull(convertDeclineAction);
        assertEquals(convertDeclineAction, ConsentAction.ActionEnum.DECLINE);

        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction.ActionEnum
                convertConsentActionNull = TosPrivacyConsentActionMapper.tosPrivacyConsentActionMapper.convertConsentAction(null);
        assertNull(convertConsentActionNull);
    }
}