package it.pagopa.pn.bff.mappers.senderdashboard;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.sender_dashboard.BffSenderDashboardDataResponse;
import it.pagopa.pn.bff.service.senderdashboard.model.DataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the DataResponse to
 * BffSenderDashboardDataResponse
 */
@Mapper(uses = {DatalakeNotificationOverviewMapper.class, DatalakeDigitalNotificationFocusMapper.class})
public interface DataResponseMapper {
    DataResponseMapper modelMapper = Mappers.getMapper(DataResponseMapper.class);

    BffSenderDashboardDataResponse toBffSenderDashboardDataResponse(DataResponse dataResponse);
}