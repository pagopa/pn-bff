package it.pagopa.pn.bff.mappers.downtimelogs;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.downtime_logs.BffPnDowntimeHistoryResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.downtime_logs.PnDowntimeEntry;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.downtime_logs.PnFunctionalityStatus;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Mapstruct mapper interface, used to map the PnDowntimeHistoryResponse to BffPnDowntimeHistoryResponse
 */
@Mapper
public interface DowntimeHistoryResponseMapper {
    // Instance of the mapper
    DowntimeHistoryResponseMapper modelMapper = Mappers.getMapper(DowntimeHistoryResponseMapper.class);

    /**
     * Maps a PnDowntimeHistoryResponse to a BffPnDowntimeHistoryResponse
     *
     * @param downtimeHistoryResponse the PnDowntimeHistoryResponse to map
     * @return the mapped BffPnDowntimeHistoryResponse
     */
    @Mapping(target = "result", source = "result", qualifiedByName = "mapResult")
    BffPnDowntimeHistoryResponse mapPnDowntimeHistoryResponse(PnDowntimeHistoryResponse downtimeHistoryResponse);

    /**
     * Map result array
     *
     * @param result the result array
     * @return the mapped result array
     */
    @Named("mapResult")
    @IterableMapping(qualifiedByName = "mapPnDowntimeEntry")
    List<PnDowntimeEntry> mapResult(List<it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnDowntimeEntry> result);

    /**
     * Map downtime event
     *
     * @param downtimeEntry downtime event
     * @return the mapped downtime event
     */
    @Named("mapPnDowntimeEntry")
    @Mapping(target = "status", source = "endDate", qualifiedByName = "mapStatus")
    PnDowntimeEntry mapPnDowntimeEntry(it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnDowntimeEntry downtimeEntry);

    /**
     * Map the incident status (if closed OK, otherwise KO)
     *
     * @param endDate the date when the incident has been closed
     * @return if incident is closed or open
     */
    @Named("mapStatus")
    default PnFunctionalityStatus mapStatus(OffsetDateTime endDate) {
        // existence of endDate is taken as source for status, takes preeminence over the status attribute
        return endDate != null ? PnFunctionalityStatus.OK : PnFunctionalityStatus.KO;
    }
}