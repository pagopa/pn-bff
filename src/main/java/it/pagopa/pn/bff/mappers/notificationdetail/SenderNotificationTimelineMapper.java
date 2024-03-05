package it.pagopa.pn.bff.mappers.notificationdetail;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationDetailTimeline;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SenderNotificationTimelineMapper {
    
    SenderNotificationTimelineMapper paTimelineMapper = Mappers.getMapper(SenderNotificationTimelineMapper.class);

    @Mapping(target = "hidden", ignore = true)
    @Mapping(target = "index", ignore = true)
    public NotificationDetailTimeline mapTimelineElement(TimelineElementV23 timelineElement);
}
