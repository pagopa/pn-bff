package it.pagopa.pn.bff.mappers.notification;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the bff NotificationStatus
 * to the delivery NotificationStatus
 */
@Mapper
public interface NotificationStatusMapper {
    NotificationStatusMapper notificationStatusMapper = Mappers.getMapper(NotificationStatusMapper.class);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatus convertDeliveryWebPANotificationStatus(NotificationStatus notificationStatus);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatus convertDeliveryRecipientNotificationStatus(NotificationStatus notificationStatus);
}