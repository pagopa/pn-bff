package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.NotificationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the bff NotificationStatusV26
 * to the delivery NotificationStatusV26
 */
@Mapper
public interface NotificationStatusMapper {
    NotificationStatusMapper notificationStatusMapper = Mappers.getMapper(NotificationStatusMapper.class);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.NotificationStatusV26 convertDeliveryWebPANotificationStatus(NotificationStatus notificationStatus);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationStatusV26 convertDeliveryRecipientNotificationStatus(NotificationStatus notificationStatus);
}