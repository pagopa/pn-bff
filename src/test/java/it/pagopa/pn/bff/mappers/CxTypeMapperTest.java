package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CxTypeMapperTest {

    @Test
    void testConvertRecipientCXType() {
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PF;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet actualConvertRecipientCXTypeResult = CxTypeMapper.cxTypeMapper.convertRecipientCXType(cxTypeAuthFleet);
        assertNotNull(actualConvertRecipientCXTypeResult);
    }

    @Test
    void testConvertPACXType() {
        CxTypeAuthFleet cxTypeAuthFleet = CxTypeAuthFleet.PA;
        it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet actualConvertPACXTypeResult = CxTypeMapper.cxTypeMapper.convertPACXType(cxTypeAuthFleet);
        assertNotNull(actualConvertPACXTypeResult);
    }
}