package it.pagopa.pn.bff.mappers.notification;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationStatusMapper {
    NotificationStatusMapper notificationStatusMapper = Mappers.getMapper(NotificationStatusMapper.class);
    it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatus convertDeliveryWebPANotificationStatus(NotificationStatus notificationStatus);
}
