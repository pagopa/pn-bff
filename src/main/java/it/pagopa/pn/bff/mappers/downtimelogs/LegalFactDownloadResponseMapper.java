package it.pagopa.pn.bff.mappers.downtimelogs;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.downtime_logs.BffLegalFactDownloadMetadataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the LegalFactDownloadMetadataResponse to BffLegalFactDownloadMetadataResponse
 */
@Mapper
public interface LegalFactDownloadResponseMapper {
    // Instance of the mapper
    LegalFactDownloadResponseMapper modelMapper = Mappers.getMapper(LegalFactDownloadResponseMapper.class);

    /**
     * Maps a LegalFactDownloadMetadataResponse to a BffLegalFactDownloadMetadataResponse
     *
     * @param legalFactDownloadMetadataResponse the LegalFactDownloadMetadataResponse to map
     * @return the mapped BffLegalFactDownloadMetadataResponse
     */
    BffLegalFactDownloadMetadataResponse mapLegalFactDownloadMetadataResponse(LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse);
}