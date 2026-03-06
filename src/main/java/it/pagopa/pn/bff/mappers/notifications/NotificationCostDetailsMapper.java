package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.notification_cost_service.model.AnalogCostDetail;
import it.pagopa.pn.bff.generated.openapi.msclient.notification_cost_service.model.NotificationCostRecipientResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationCostDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the NotificationCostRecipientResponse to the BffNotificationCostDetails
 */
@Mapper
public interface NotificationCostDetailsMapper {
    NotificationCostDetailsMapper modelMapper = Mappers.getMapper(NotificationCostDetailsMapper.class);

    @Mapping(source = "totalCost.costWithVat", target = "totalCost")
    @Mapping(source = "totalCost.details.baseCostDetail.cost", target = "baseCost")
    @Mapping(source = "totalCost.details.analogCostDetail.costWithVat", target = "analogCost")
    @Mapping(target = "numberOfAnalogCost", source = "totalCost.details.analogCostDetail")
    @Mapping(target = "status", ignore = true)
    BffNotificationCostDetails mapCostDetails(NotificationCostRecipientResponse response);

    default Integer mapNumberOfAnalogCost(AnalogCostDetail analogCostDetail) {
        return analogCostDetail != null
                ? analogCostDetail.getAnalogCostComponents().size()
                : 0;
    }
}