package it.pagopa.pn.bff.mappers.notification;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponseV1;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationSentMapper {
    NotificationSentMapper modelMapper = Mappers.getMapper(NotificationSentMapper.class);

    /**
     * Maps a NotificationSearchResponse to a BffNotificationsResponseV1
     *
     * @param notificationSearchResponse The NotificationSearchResponse to map
     * @return The mapped BffNotificationsResponseV1
     */
    BffNotificationsResponseV1 toBffNotificationsResponseV1(NotificationSearchResponse notificationSearchResponse);

}
