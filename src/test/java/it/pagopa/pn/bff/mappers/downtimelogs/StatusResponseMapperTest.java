package it.pagopa.pn.bff.mappers.downtimelogs;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnStatusResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPnStatusResponse;
import it.pagopa.pn.bff.mocks.DowntimeLogsMock;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class StatusResponseMapperTest {

    private final DowntimeLogsMock downtimeLogsMock = new DowntimeLogsMock();

    @Test
    void testStatusResponseMapperOK() {
        PnStatusResponse statusResponse = downtimeLogsMock.getStatusMockOK();

        BffPnStatusResponse bffPnStatusResponse = StatusResponseMapper.modelMapper.mapPnStatusResponse(statusResponse);
        assertNotNull(bffPnStatusResponse);
        assertEquals(true, bffPnStatusResponse.getAppIsFullyOperative());
        assertEquals(bffPnStatusResponse.getLastCheckTimestamp(), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));

        BffPnStatusResponse bffPnStatusResponseNull = StatusResponseMapper.modelMapper.mapPnStatusResponse(null);
        assertNull(bffPnStatusResponseNull);
    }

    @Test
    void testStatusResponseMapperKO() {
        PnStatusResponse statusResponse = downtimeLogsMock.getStatusMockKO();

        BffPnStatusResponse bffPnStatusResponse = StatusResponseMapper.modelMapper.mapPnStatusResponse(statusResponse);
        assertNotNull(bffPnStatusResponse);
        assertEquals(false, bffPnStatusResponse.getAppIsFullyOperative());
        assertEquals(bffPnStatusResponse.getLastCheckTimestamp(), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));

        BffPnStatusResponse bffPnStatusResponseNull = StatusResponseMapper.modelMapper.mapPnStatusResponse(null);
        assertNull(bffPnStatusResponseNull);
    }
}