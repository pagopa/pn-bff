package it.pagopa.pn.bff.mappers.notification;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationReceivedMapper {
    NotificationReceivedMapper modelMapper = Mappers.getMapper(NotificationReceivedMapper.class);

    /**
     * Maps a NotificationSearchResponse to a BffNotificationsResponse
     * @param notificationSearchResponse the NotificationSearchResponse to map
     * @return the mapped BffNotificationsResponse
     */
    BffNotificationsResponse toBffNotificationsResponse(NotificationSearchResponse notificationSearchResponse);
}
