package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationDetailTimeline;
import it.pagopa.pn.bff.utils.NotificationDetailUtility;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the FullSentNotificationV23
 * to the BffFullNotificationV1
 */
@Mapper(uses = {SenderNotificationTimelineMapper.class})
public interface NotificationSentDetailMapper {

    // Instance of the mapper
    NotificationSentDetailMapper modelMapper = Mappers.getMapper(NotificationSentDetailMapper.class);

    /**
     * Maps a FullSentNotificationV23 to a BffFullNotificationV1
     *
     * @param notification the FullSentNotificationV23 to map
     * @return the mapped BffFullNotificationV1
     */
    BffFullNotificationV1 mapSentNotificationDetail(FullSentNotificationV23 notification);

    /**
     * @see it.pagopa.pn.bff.utils.NotificationDetailUtility#cleanRelatedTimelineElements(BffFullNotificationV1)
     */
    @AfterMapping
    default void cleanRelatedTimelineElements(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.cleanRelatedTimelineElements(bffFullNotificationV1);
    }

    /**
     * @see it.pagopa.pn.bff.utils.NotificationDetailUtility#populateOtherDocuments(BffFullNotificationV1)
     */
    @AfterMapping
    default void populateOtherDocuments(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.populateOtherDocuments(bffFullNotificationV1);
    }

    /**
     * @see it.pagopa.pn.bff.utils.NotificationDetailUtility#checkRADDInTimeline(BffFullNotificationV1)
     */
    @AfterMapping
    default void checkRADDInTimeline(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.checkRADDInTimeline(bffFullNotificationV1);
    }

    /**
     * @see it.pagopa.pn.bff.utils.NotificationDetailUtility#insertCancelledStatusInTimeline(BffFullNotificationV1)
     */
    @AfterMapping
    default void insertCancelledStatusInTimeline(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.insertCancelledStatusInTimeline(bffFullNotificationV1);
    }

    /**
     * Add index and hidden properties to timeline elements.<br />
     * Hidden is used to hide those categories that are not shown by frontend.<br />
     * Index is used to sort concurrent timeline elements.
     *
     * @param bffFullNotificationV1 the BffFullNotificationV1 to map
     */
    @AfterMapping
    default void setTimelineIndexAndHidden(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        for (int i = 0; i < bffFullNotificationV1.getTimeline().size(); i++) {
            BffNotificationDetailTimeline timelineElement = bffFullNotificationV1.getTimeline().get(i);
            timelineElement.setIndex(i);
            timelineElement.setHidden(!NotificationDetailUtility.timelineElementMustBeShown(timelineElement));
        }
    }

    /**
     * @see it.pagopa.pn.bff.utils.NotificationDetailUtility#populateMacroSteps(BffFullNotificationV1)
     */
    @AfterMapping
    default void populateMacroStep(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.populateMacroSteps(bffFullNotificationV1);
    }

    /**
     * Sort the notification status history by activeFrom date
     * From delivery, the statuses of the notification are sorted ascending (from the oldest to the earliest)
     * Front-end wants them ordered descending (from the earliest to the oldest) instead
     *
     * @param bffFullNotificationV1 the BffFullNotificationV1 to map
     */
    @AfterMapping
    default void sortNotificationStatusHistory(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        bffFullNotificationV1.getNotificationStatusHistory().sort((o1, o2) ->
                o2.getActiveFrom().toInstant().toEpochMilli() >= o1.getActiveFrom().toInstant().toEpochMilli() ? 1 : -1
        );
    }
}