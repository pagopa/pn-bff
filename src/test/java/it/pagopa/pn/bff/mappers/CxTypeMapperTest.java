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
    void testConvertDeliveryB2bPACXType() {
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet actualConvertPACXTypeResult = CxTypeMapper.cxTypeMapper.convertDeliveryB2bPACXType(cxTypeAuthFleet);
        assertNotNull(actualConvertPACXTypeResult);
        assertEquals(actualConvertPACXTypeResult.getValue(), cxTypeAuthFleet.getValue());
    }

    @Test
    void testConvertDeliveryWebPACXType() {
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet actualConvertPACXTypeResult = CxTypeMapper.cxTypeMapper.convertDeliveryWebPACXType(cxTypeAuthFleet);
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
    void testConvertExternalRegistriesSelfCare(){
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet actualConvertExternalRegistriesSelfCare = CxTypeMapper.cxTypeMapper.convertExternalRegistriesCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertExternalRegistriesSelfCare);
        assertEquals(actualConvertExternalRegistriesSelfCare.getValue(), cxTypeAuthFleet.getValue());
    }
}