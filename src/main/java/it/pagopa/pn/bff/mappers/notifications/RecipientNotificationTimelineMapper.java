package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementV26;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationDetailTimeline;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the TimelineElementV26 from the FullReceivedNotificationV25
 * to the NotificationDetailTimeline
 */
@Mapper
public interface RecipientNotificationTimelineMapper {

    RecipientNotificationTimelineMapper recipientTimelineMapper = Mappers.getMapper(RecipientNotificationTimelineMapper.class);

    @Mapping(target = "hidden", ignore = true)
    @Mapping(target = "index", ignore = true)
    public BffNotificationDetailTimeline mapTimelineElement(TimelineElementV26 timelineElement);
}