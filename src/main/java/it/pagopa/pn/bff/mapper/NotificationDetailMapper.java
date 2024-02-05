package it.pagopa.pn.bff.mapper;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullReceivedNotificationV23;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationDetailMapper {
    ModelMapper modelMapper = new ModelMapper();

    public BffFullReceivedNotificationV23 mapNotificationDetail(FullReceivedNotificationV23 notification) {
        return modelMapper.map(notification, BffFullReceivedNotificationV23.class);
    }
}
