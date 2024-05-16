package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.UpdateRequestDto;
import it.pagopa.pn.bff.mocks.MandateMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateMandateMapperTest {
    private final MandateMock mandateMock = new MandateMock();

    @Test
    void testUpdateMandateMapper() {
        UpdateRequestDto updateRequestDto = UpdateMandateMapper.modelMapper.mapRequest(mandateMock.getBffUpdateRequestMock());
        assertNotNull(updateRequestDto);
        assertEquals(updateRequestDto, mandateMock.getUpdateRequestMock());

        UpdateRequestDto updateRequestDtoNull = UpdateMandateMapper.modelMapper.mapRequest(null);
        assertNull(updateRequestDtoNull);
    }
}