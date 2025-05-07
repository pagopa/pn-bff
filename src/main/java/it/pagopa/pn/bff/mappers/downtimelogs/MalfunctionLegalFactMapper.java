package it.pagopa.pn.bff.mappers.downtimelogs;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnStatusUpdateEvent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.downtime_logs.BffPnDowntimeMalfunctionLegalFact;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the LegalFactDownloadMetadataResponse to BffLegalFactDownloadMetadataResponse
 */
@Mapper
public interface MalfunctionLegalFactMapper {
    // Instance of the mapper
    MalfunctionLegalFactMapper modelMapper = Mappers.getMapper(MalfunctionLegalFactMapper.class);

    PnStatusUpdateEvent mapBffPnDowntimeMalfunctionLegalFact(BffPnDowntimeMalfunctionLegalFact malfunctionLegalFact);
}

