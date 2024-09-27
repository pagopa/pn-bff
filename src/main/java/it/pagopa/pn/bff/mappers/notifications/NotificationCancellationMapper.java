package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.RequestStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffRequestStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the RequestStatus to the BffRequestStatus
 */
@Mapper
public interface NotificationCancellationMapper {
    // Instance of the mapper
    NotificationCancellationMapper modelMapper = Mappers.getMapper(NotificationCancellationMapper.class);

    /**
     * Maps a RequestStatus to a BffRequestStatus
     *
     * @param notification the RequestStatus to map
     * @return the mapped BffRequestStatus
     */
    BffRequestStatus mapNotificationCancellation(RequestStatus notification);
}