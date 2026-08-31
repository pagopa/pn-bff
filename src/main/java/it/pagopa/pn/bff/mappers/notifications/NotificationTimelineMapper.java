package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.*;
import it.pagopa.pn.bff.utils.NotificationTimelineUtility;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationTimelineMapper {

    // Instance of the mapper
    NotificationTimelineMapper modelMapper = Mappers.getMapper(NotificationTimelineMapper.class);

    /**
     * Maps the existing notification detail to the new timeline response.
     * notificationStatusHistory is populated in the after-mapping phase.
     */
    @Mapping(source = "recipients", target = "recipients")
    @Mapping(target = "notificationStatusHistory", ignore = true)
    @Mapping(target = "isCancelled", ignore = true)
    BffNotificationTimelineResponse mapNotificationTimeline(BffFullNotificationV1 notification);

    /**
     * Sets the flag indicating whether the timeline contains a cancellation event.
     */
    @AfterMapping
    default void populateIsCancelled(
            BffFullNotificationV1 notification,
            @MappingTarget BffNotificationTimelineResponse response) {

        response.setIsCancelled(
                NotificationTimelineUtility.hasCancellationInTimeline(notification)
        );
    }

    /**
     * Maps the simple status-history fields.
     * Steps are populated by NotificationTimelineUtility.
     */
    @Mapping(source = "recipient", target = "viewedByMandate")
    @Mapping(target = "steps", ignore = true)
    BffNotificationTimelineStatusHistory mapStatusHistory(BffNotificationStatusHistory statusHistory);

    /**
     * Maps an existing timeline element to the new event model.
     */
    @Mapping(target = "isHidden", expression = "java(Boolean.TRUE.equals(timelineElement.getHidden()))")
    BffNotificationTimelineEvent mapTimelineElement(BffNotificationDetailTimeline timelineElement);

    /**
     * Applies the transformations required by the new timeline.
     */
    @AfterMapping
    default void populateNotificationStatusHistory(
            BffFullNotificationV1 notification,
            @MappingTarget BffNotificationTimelineResponse response) {

        NotificationTimelineUtility.populateNotificationStatusHistory(
                notification,
                response,
                this
        );
    }
}
