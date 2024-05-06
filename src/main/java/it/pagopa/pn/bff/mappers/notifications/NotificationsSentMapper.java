package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the NotificationSearchResponse
 * to the BffNotificationsResponse
 */
@Mapper
public interface NotificationsSentMapper {
    NotificationsSentMapper modelMapper = Mappers.getMapper(NotificationsSentMapper.class);

    /**
     * Maps a NotificationSearchResponse to a BffNotificationsResponse
     *
     * @param notificationSearchResponse the NotificationSearchResponse to map
     * @return the mapped BffNotificationsResponse
     */
    BffNotificationsResponse toBffNotificationsResponse(NotificationSearchResponse notificationSearchResponse);

}