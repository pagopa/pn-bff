package it.pagopa.pn.bff.mappers.notification;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationSentMapper {
    NotificationSentMapper modelMapper = Mappers.getMapper(NotificationSentMapper.class);

    /**
     * Maps a NotificationSearchResponse to a BffNotificationsResponse
     *
     * @param notificationSearchResponse The NotificationSearchResponse to map
     * @return The mapped BffNotificationsResponse
     */
    BffNotificationsResponse toBffNotificationsResponse(NotificationSearchResponse notificationSearchResponse);

}
