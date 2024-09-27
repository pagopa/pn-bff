package it.pagopa.pn.bff.mappers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CxTypeMapperTest {

    @Test
    void testConvertDeliveryRecipientCXType() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PF;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet actualConvertRecipientCXTypeResult = CxTypeMapper.cxTypeMapper.convertDeliveryRecipientCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertRecipientCXTypeResult);
        assertEquals(actualConvertRecipientCXTypeResult.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertDeliveryB2bPACXType() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet actualConvertPACXTypeResult = CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(cxTypeAuthFleet);
        assertNotNull(actualConvertPACXTypeResult);
        assertEquals(actualConvertPACXTypeResult.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertDeliveryWebPACXType() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet actualConvertPACXTypeResult = CxTypeMapper.cxTypeMapper.convertDeliveryWebPACXType(cxTypeAuthFleet);
        assertNotNull(actualConvertPACXTypeResult);
        assertEquals(actualConvertPACXTypeResult.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertApiKeysPACXType() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet actualConvertPACXTypeResult = CxTypeMapper.cxTypeMapper.convertApiKeysPACXType(cxTypeAuthFleet);
        assertNotNull(actualConvertPACXTypeResult);
        assertEquals(actualConvertPACXTypeResult.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertExternalRegistriesSelfCare() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet actualConvertExternalRegistriesSelfCare = CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertExternalRegistriesSelfCare);
        assertEquals(actualConvertExternalRegistriesSelfCare.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertExternalRegistriesPayments() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet actualConvertExternalRegistriesSelfCare = CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertExternalRegistriesSelfCare);
        assertEquals(actualConvertExternalRegistriesSelfCare.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertUserAttributes() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_attributes.CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet actualConvertUserAttributes = CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertUserAttributes);
        assertEquals(actualConvertUserAttributes.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertDeliveryPush() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet actualConvertDeliveryPush = CxTypeMapper.cxTypeMapper.convertDeliveryPushCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertDeliveryPush);
        assertEquals(actualConvertDeliveryPush.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertMandate() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.CxTypeAuthFleet.PF;
        it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.CxTypeAuthFleet actualConvertMandate = CxTypeMapper.cxTypeMapper.convertMandateCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertMandate);
        assertEquals(actualConvertMandate.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertPublicKeysPGCXType() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG;
        it.pagopa.pn.bff.generated.openapi.msclient.publickey_pg.model.CxTypeAuthFleet actualConvertPGCXTypeResult = CxTypeMapper.cxTypeMapper.convertPublicKeysPGCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertPGCXTypeResult);
        assertEquals(actualConvertPGCXTypeResult.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertUserAttributesCXType() {
        it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet cxTypeAuthFleet = it.pagopa.pn.bff.generated.openapi.server.v1.dto.apikeys.CxTypeAuthFleet.PG;
        it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet actualConvertPGCXTypeResult = CxTypeMapper.cxTypeMapper.convertUserAttributesCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertPGCXTypeResult);
        assertEquals(actualConvertPGCXTypeResult.getValue(), cxTypeAuthFleet.getValue());
    }
}