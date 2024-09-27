package it.pagopa.pn.bff.mappers.senderdashboard;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.sender_dashboard.BffSenderDashboardDigitalNotificationFocus;
import it.pagopa.pn.bff.service.senderdashboard.model.DatalakeDigitalNotificationFocus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the DatalakeDigitalNotificationFocus to
 * BffSenderDashboardDigitalNotificationFocus
 */
@Mapper
public interface DatalakeDigitalNotificationFocusMapper {
    DatalakeDigitalNotificationFocusMapper modelMapper = Mappers.getMapper(DatalakeDigitalNotificationFocusMapper.class);

    @Named("normalizeInt")
    static Integer normalizeInt(String value) {
        return value != null
                ? Integer.parseInt(value.replace(".", ""))
                : null;
    }

    @Mapping(source = "failedAttemptsCount", target = "failedAttemptsCount", qualifiedByName = "normalizeInt")
    @Mapping(source = "notificationsCount", target = "notificationsCount", qualifiedByName = "normalizeInt")
    BffSenderDashboardDigitalNotificationFocus toBffSenderDashboardDigitalNotificationFocus(
            DatalakeDigitalNotificationFocus datalakeDigitalNotificationFocus);
}