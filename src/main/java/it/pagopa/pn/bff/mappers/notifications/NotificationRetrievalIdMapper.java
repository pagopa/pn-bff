package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.emd.model.RetrievalPayload;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCheckTPPResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the RetrievalPayload to the BffCheckTPPResponse
 */
@Mapper
public interface NotificationRetrievalIdMapper {

    NotificationRetrievalIdMapper modelMapper = Mappers.getMapper(NotificationRetrievalIdMapper.class);

    /**
     * Maps a RetrievalPayload to a BffCheckTPPResponse
     *
     * @param retrievalPayload the RetrievalPayload to map
     * @return the mapped BffCheckTPPResponse
     */
    BffCheckTPPResponse toBffCheckTPPResponse(RetrievalPayload retrievalPayload);
}