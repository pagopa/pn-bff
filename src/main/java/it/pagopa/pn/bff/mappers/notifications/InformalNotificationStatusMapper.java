package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.InformalNotificationStatusV1;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the bff InformalNotificationStatusV1
 * to the delivery InformalNotificationStatusV1
 */
@Mapper
public interface InformalNotificationStatusMapper {
    InformalNotificationStatusMapper informalNotificationStatusMapper = Mappers.getMapper(InformalNotificationStatusMapper.class);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_web.model.InformalNotificationStatusV1 convertDeliveryInformalPAWebNotificationStatus(InformalNotificationStatusV1 notificationStatus);
}