package it.pagopa.pn.bff.mappers.inforecipient;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaSummary;
import it.pagopa.pn.bff.mocks.RecipientInfoMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaListMapperTest {
    private final RecipientInfoMock recipientInfoMock = new RecipientInfoMock();

    @Test
    void testPaListMapper() {
        PaSummary paSummary = recipientInfoMock.getPaSummary();

        it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaSummary bffPaSummary = PaListMapper.modelMapper.mapPaList(paSummary);
        assertNotNull(bffPaSummary);

        assertEquals(bffPaSummary.getId(), paSummary.getId());
        assertEquals(bffPaSummary.getName(), paSummary.getName());

        it.pagopa.pn.bff.generated.openapi.server.v1.dto.PaSummary bffPaSummaryNull = PaListMapper.modelMapper.mapPaList(null);
        assertNull(bffPaSummaryNull);
    }
}
