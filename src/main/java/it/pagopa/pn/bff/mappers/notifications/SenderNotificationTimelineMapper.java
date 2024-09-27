package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.TimelineElementV24;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationDetailTimeline;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the TimelineElementV24 from the FullSentNotificationV24
 * to the NotificationDetailTimeline
 */
@Mapper
public interface SenderNotificationTimelineMapper {

    SenderNotificationTimelineMapper paTimelineMapper = Mappers.getMapper(SenderNotificationTimelineMapper.class);

    @Mapping(target = "hidden", ignore = true)
    @Mapping(target = "index", ignore = true)
    public BffNotificationDetailTimeline mapTimelineElement(TimelineElementV24 timelineElement);
}