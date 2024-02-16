package it.pagopa.pn.bff.mapper;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullSentNotificationV1;
import org.mapstruct.Mapper;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

@Mapper(mappingControl = DeepClone.class, uses = DateMapper.class)
public interface NotificationDetailMapper {

    NotificationDetailMapper modelMapper = Mappers.getMapper(NotificationDetailMapper.class);
    
    BffFullReceivedNotificationV1 mapNotificationDetail(FullReceivedNotificationV23 notification);

    BffFullSentNotificationV1 mapSentNotificationDetail(FullSentNotificationV23 notification);
}