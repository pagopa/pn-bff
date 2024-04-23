package it.pagopa.pn.bff.mappers.downtimelogs;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPnDowntimeHistoryResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.PnFunctionalityStatus;
import it.pagopa.pn.bff.mocks.DowntimeLogsMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DowntimeHistoryResponseMapperTest {
    private final DowntimeLogsMock downtimeLogsMock = new DowntimeLogsMock();

    @Test
    void testDowntimeHistoryResponseMapper() {
        PnDowntimeHistoryResponse downtimeHistoryResponse = downtimeLogsMock.getDowntimeHistoryMock();

        BffPnDowntimeHistoryResponse bffPnDowntimeHistoryResponse = DowntimeHistoryResponseMapper.modelMapper.mapPnDowntimeHistoryResponse(downtimeHistoryResponse);
        assertNotNull(bffPnDowntimeHistoryResponse);
        assertEquals(bffPnDowntimeHistoryResponse.getNextPage(), downtimeHistoryResponse.getNextPage());
        for (int i = 0; i < bffPnDowntimeHistoryResponse.getResult().size(); i++) {
            assertEquals(bffPnDowntimeHistoryResponse.getResult().get(i).getStartDate(), downtimeHistoryResponse.getResult().get(i).getStartDate());
            assertEquals(bffPnDowntimeHistoryResponse.getResult().get(i).getEndDate(), downtimeHistoryResponse.getResult().get(i).getEndDate());
            PnFunctionalityStatus status = downtimeHistoryResponse.getResult().get(i).getEndDate() != null ? PnFunctionalityStatus.OK : PnFunctionalityStatus.KO;
            assertEquals(bffPnDowntimeHistoryResponse.getResult().get(i).getStatus(), status);
            assertEquals(bffPnDowntimeHistoryResponse.getResult().get(i).getFunctionality().getValue(), downtimeHistoryResponse.getResult().get(i).getFunctionality().getValue());
            assertEquals(bffPnDowntimeHistoryResponse.getResult().get(i).getLegalFactId(), downtimeHistoryResponse.getResult().get(i).getLegalFactId());
            assertEquals(bffPnDowntimeHistoryResponse.getResult().get(i).getFileAvailable(), downtimeHistoryResponse.getResult().get(i).getFileAvailable());
        }

        BffPnDowntimeHistoryResponse bffPnDowntimeHistoryResponseNull = DowntimeHistoryResponseMapper.modelMapper.mapPnDowntimeHistoryResponse(null);
        assertNull(bffPnDowntimeHistoryResponseNull);
    }
}