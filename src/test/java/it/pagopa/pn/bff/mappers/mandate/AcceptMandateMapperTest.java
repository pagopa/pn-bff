package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.AcceptRequestDto;
import it.pagopa.pn.bff.mocks.MandateMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AcceptMandateMapperTest {
    private final MandateMock mandateMock = new MandateMock();

    @Test
    void testAcceptMandateMapper() {
        AcceptRequestDto acceptRequestDto = AcceptMandateMapper.modelMapper.mapRequest(mandateMock.getBffAcceptRequestMock());
        assertNotNull(acceptRequestDto);
        assertEquals(acceptRequestDto, mandateMock.getAcceptRequestMock());

        AcceptRequestDto acceptRequestDtoNull = AcceptMandateMapper.modelMapper.mapRequest(null);
        assertNull(acceptRequestDtoNull);
    }
}