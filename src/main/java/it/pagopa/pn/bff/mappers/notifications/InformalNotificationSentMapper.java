package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullSentInformalNotificationV1;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InformalNotificationSentMapper {

    InformalNotificationSentMapper modelMapper = Mappers.getMapper(InformalNotificationSentMapper.class);

    BffFullSentInformalNotificationV1 mapSentInformalNotificationDetail(
            FullSentInformalNotificationV1 notification
    );
}