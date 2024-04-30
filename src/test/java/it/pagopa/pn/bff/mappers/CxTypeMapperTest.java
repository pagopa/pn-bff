package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CxTypeMapperTest {

    @Test
    void testConvertDeliveryRecipientCXType() {
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PF;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet actualConvertRecipientCXTypeResult = CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertRecipientCXTypeResult);
        assertEquals(actualConvertRecipientCXTypeResult.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertDeliveryPACXType() {
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet actualConvertPACXTypeResult = CxTypeMapper.cxTypeMapper.convertDeliveryPACXType(cxTypeAuthFleet);
        assertNotNull(actualConvertPACXTypeResult);
        assertEquals(actualConvertPACXTypeResult.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertApiKeysPACXType() {
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet actualConvertPACXTypeResult = CxTypeMapper.cxTypeMapper.convertApiKeysPACXType(cxTypeAuthFleet);
        assertNotNull(actualConvertPACXTypeResult);
        assertEquals(actualConvertPACXTypeResult.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertExternalRegistriesSelfCare() {
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet actualConvertExternalRegistriesSelfCare = CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertExternalRegistriesSelfCare);
        assertEquals(actualConvertExternalRegistriesSelfCare.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertUserAttributes() {
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet actualConvertUserAttributes = CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertUserAttributes);
        assertEquals(actualConvertUserAttributes.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertDeliveryPush() {
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet actualConvertDeliveryPush = CxTypeMapper.cxTypeMapper.convertDeliveryPushCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertDeliveryPush);
        assertEquals(actualConvertDeliveryPush.getValue(), cxTypeAuthFleet.getValue());
    }
}