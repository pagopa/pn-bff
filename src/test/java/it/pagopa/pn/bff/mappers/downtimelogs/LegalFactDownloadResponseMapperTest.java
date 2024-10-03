package it.pagopa.pn.bff.mappers.downtimelogs;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.downtime_logs.BffLegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.mocks.DowntimeLogsMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegalFactDownloadResponseMapperTest {
    private final DowntimeLogsMock downtimeLogsMock = new DowntimeLogsMock();

    @Test
    void testLegalFactDownloadResponseMapper() {
        LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = downtimeLogsMock.getLegalFactMetadataMock();

        BffLegalFactDownloadMetadataResponse bffLegalFactDownloadMetadataResponse = LegalFactDownloadResponseMapper.modelMapper.mapLegalFactDownloadMetadataResponse(legalFactDownloadMetadataResponse);
        assertNotNull(bffLegalFactDownloadMetadataResponse);
        assertEquals(bffLegalFactDownloadMetadataResponse.getContentLength(), legalFactDownloadMetadataResponse.getContentLength());
        assertEquals(bffLegalFactDownloadMetadataResponse.getFilename(), legalFactDownloadMetadataResponse.getFilename());
        assertEquals(bffLegalFactDownloadMetadataResponse.getUrl(), legalFactDownloadMetadataResponse.getUrl());
        assertEquals(bffLegalFactDownloadMetadataResponse.getRetryAfter(), legalFactDownloadMetadataResponse.getRetryAfter());

        BffLegalFactDownloadMetadataResponse bffLegalFactDownloadMetadataResponseNull = LegalFactDownloadResponseMapper.modelMapper.mapLegalFactDownloadMetadataResponse(null);
        assertNull(bffLegalFactDownloadMetadataResponseNull);
    }
}