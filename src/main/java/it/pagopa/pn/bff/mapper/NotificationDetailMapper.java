package it.pagopa.pn.bff.mapper;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV21;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV21;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationDetailMapper {
    ModelMapper modelMapper = new ModelMapper();

    public BffFullReceivedNotificationV21 mapNotificationDetail(FullReceivedNotificationV21 notification) {
        return modelMapper.map(notification, BffFullReceivedNotificationV21.class);
    }
}
