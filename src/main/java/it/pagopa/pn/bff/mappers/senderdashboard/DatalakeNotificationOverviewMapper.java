package it.pagopa.pn.bff.mappers.senderdashboard;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffSenderDashboardNotificationOverview;
import it.pagopa.pn.bff.service.senderdashboard.model.DatalakeNotificationOverview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the DatalakeNotificationOverview to
 * BffSenderDashboardNotificationOverview
 */
@Mapper
public interface DatalakeNotificationOverviewMapper {
    DatalakeNotificationOverviewMapper modelMapper = Mappers.getMapper(DatalakeNotificationOverviewMapper.class);

    @Mapping(source = "attemptCountPerDigitalNotification", target = "attemptCountPerDigitalNotification",
            qualifiedByName = "normalizeInt")
    @Mapping(source = "notificationsCount", target = "notificationsCount", qualifiedByName = "normalizeInt")
    @Mapping(source = "deliveryTime", target = "deliveryTime", qualifiedByName = "normalizeDouble")
    @Mapping(source = "viewTime", target = "viewTime", qualifiedByName = "normalizeDouble")
    @Mapping(source = "refinementTime", target = "refinementTime", qualifiedByName = "normalizeDouble")
    @Mapping(source = "validationTime", target = "validationTime", qualifiedByName = "normalizeDouble")
    BffSenderDashboardNotificationOverview toBffSenderDashboardNotificationOverview(
            DatalakeNotificationOverview datalakeNotificationOverview);

    @Named("normalizeInt")
    static int normalizeInt(String value) {
        return Integer.parseInt(value.replace(".", ""));
    }

    @Named("normalizeDouble")
    static double normalizeDouble(String value) {
        return Double.parseDouble(value.replace(".", "").replace(",", "."));
    }
}
