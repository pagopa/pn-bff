package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullSentInformalNotificationTimelineV1;
import it.pagopa.pn.bff.utils.InformalNotificationTimelineUtility;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the FullSentInformalNotificationV1
 * to the BffFullSentInformalNotificationTimelineV1
 */
@Mapper
public interface InformalNotificationTimelineMapper {

    // Instance of the mapper
    InformalNotificationTimelineMapper modelMapper = Mappers.getMapper(InformalNotificationTimelineMapper.class);

    /**
     * Maps a FullSentInformalNotificationV1 to a BffFullSentInformalNotificationTimelineV1.
     *
     * @param notification the FullSentInformalNotificationV1 to map
     * @return the mapped BffFullSentInformalNotificationTimelineV1
     */
    @Mapping(target = "notificationStatusHistory", ignore = true)
    @Mapping(target = "communicationOutcomes", ignore = true)
    BffFullSentInformalNotificationTimelineV1 mapSentInformalNotificationTimeline(FullSentInformalNotificationV1 notification);

    /**
     * Sets the communication outcomes computed from the notification's timeline
     */
    @AfterMapping
    default void populateCommunicationOutcomes(
            FullSentInformalNotificationV1 notification,
            @MappingTarget BffFullSentInformalNotificationTimelineV1 target) {

        target.setCommunicationOutcomes(
                InformalNotificationTimelineUtility.computeCommunicationOutcomes(notification.getTimeline())
        );
    }
}
