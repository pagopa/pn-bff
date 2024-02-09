package it.pagopa.pn.bff.mapper;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullSentNotificationV23;
import org.mapstruct.Mapper;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

@Mapper(mappingControl = DeepClone.class, uses = DateMapper.class)
public interface NotificationDetailMapper {

    NotificationDetailMapper modelMapper = Mappers.getMapper(NotificationDetailMapper.class);
    
    BffFullReceivedNotificationV23 mapNotificationDetail(FullReceivedNotificationV23 notification);

    BffFullSentNotificationV23 mapSentNotificationDetail(FullSentNotificationV23 notification);
}
