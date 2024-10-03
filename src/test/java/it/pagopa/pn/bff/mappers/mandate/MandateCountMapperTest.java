package it.pagopa.pn.bff.mappers.mandate;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.mandate.BffMandatesCount;
import it.pagopa.pn.bff.mocks.MandateMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MandateCountMapperTest {
    private final MandateMock mandateMock = new MandateMock();

    @Test
    void testMandateCountMapper() {
        BffMandatesCount bffMandatesCount = MandateCountMapper.modelMapper.mapCount(mandateMock.getCountMock());
        assertNotNull(bffMandatesCount);
        assertEquals(bffMandatesCount.getValue(), mandateMock.getCountMock().getValue());

        BffMandatesCount bffMandatesCountNull = MandateCountMapper.modelMapper.mapCount(null);
        assertNull(bffMandatesCountNull);
    }
}