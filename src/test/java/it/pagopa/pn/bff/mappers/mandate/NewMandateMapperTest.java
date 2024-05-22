package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.MandateDto;
import it.pagopa.pn.bff.mocks.MandateMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewMandateMapperTest {
    private final MandateMock mandateMock = new MandateMock();

    @Test
    void testNewMandateMapper() {
        MandateDto mandateDto = NewMandateMapper.modelMapper.mapRequest(mandateMock.getBffNewMandateRequestMock());
        assertNotNull(mandateDto);
        assertEquals(mandateDto, mandateMock.getNewMandateRequestMock());

        MandateDto mandateDtoNull = NewMandateMapper.modelMapper.mapRequest(null);
        assertNull(mandateDtoNull);
    }
}