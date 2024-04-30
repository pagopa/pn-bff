package it.pagopa.pn.bff.mappers.notification;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponseV1;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationReceivedMapper {
    NotificationReceivedMapper modelMapper = Mappers.getMapper(NotificationReceivedMapper.class);
    BffNotificationsResponseV1 toBffNotificationsResponseV1(NotificationSearchResponse notificationSearchResponse);
}
