package it.pagopa.pn.bff.mappers.notificationdetail;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.TimelineElementV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationDetailTimeline;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the TimelineElementV23 from the FullSentNotificationV23
 * to the NotificationDetailTimeline
 */
@Mapper
public interface SenderNotificationTimelineMapper {

    SenderNotificationTimelineMapper paTimelineMapper = Mappers.getMapper(SenderNotificationTimelineMapper.class);

    @Mapping(target = "hidden", ignore = true)
    @Mapping(target = "index", ignore = true)
    public BffNotificationDetailTimeline mapTimelineElement(TimelineElementV23 timelineElement);
}