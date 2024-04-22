package it.pagopa.pn.bff.mappers.downtimelogs;

import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnDowntimeEntry;
import it.pagopa.pn.bff.generated.openapi.msclient.downtime_logs.model.PnStatusResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffPnStatusResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Mapstruct mapper interface, used to map the PnStatusResponse to BffPnStatusResponse
 */
@Mapper
public interface StatusResponseMapper {
    // Instance of the mapper
    StatusResponseMapper modelMapper = Mappers.getMapper(StatusResponseMapper.class);

    /**
     * Maps a PnStatusResponse to a BffPnStatusResponse
     *
     * @param statusResponse the PnStatusResponse to map
     * @return the mapped BffPnStatusResponse
     */
    @Mapping(target = "appIsFullyOperative", source = "openIncidents", qualifiedByName = "mapAppIsFullyOperative")
    BffPnStatusResponse mapPnStatusResponse(PnStatusResponse statusResponse);

    /**
     * Map the appIsFullyOperative flag
     *
     * @param openIncidents list of open incidents
     * @return if app is fully operative or not
     */
    @Named("mapAppIsFullyOperative")
    default Boolean mapAppIsFullyOperative(List<PnDowntimeEntry> openIncidents) {
        return openIncidents.isEmpty();
    }

    /**
     * Calculate the timestamp of the last check
     *
     * @param bffPnStatusResponse the response to update with the calculated timestamp
     */
    @AfterMapping
    default void mapLastCheckTimestamp(@MappingTarget BffPnStatusResponse bffPnStatusResponse) {
        bffPnStatusResponse.setLastCheckTimestamp(OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }
}