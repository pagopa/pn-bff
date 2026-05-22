package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV28;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationDetailTimeline;
import it.pagopa.pn.bff.utils.NotificationDetailUtility;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the FullReceivedNotificationV28
 * to the BffFullNotificationV1
 */
@Mapper(uses = {RecipientNotificationTimelineMapper.class})
public interface NotificationReceivedDetailMapper {

    // Instance of the mapper
    NotificationReceivedDetailMapper modelMapper = Mappers.getMapper(NotificationReceivedDetailMapper.class);

    /**
     * Maps a FullReceivedNotificationV28 to a BffFullNotificationV1
     *
     * @param notification the FullReceivedNotificationV28 to map
     * @return the mapped BffFullNotificationV1
     */
    BffFullNotificationV1 mapReceivedNotificationDetail(FullReceivedNotificationV28 notification);

    /**
     * @see it.pagopa.pn.bff.utils.NotificationDetailUtility#insertInvalidateElementsInTimeline(BffFullNotificationV1)
     */
    @AfterMapping
    default void insertInvalidateElementsInTimeline(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.insertInvalidateElementsInTimeline(bffFullNotificationV1);
    }

    /**
     * @see it.pagopa.pn.bff.utils.NotificationDetailUtility#insertReworkedStatus(BffFullNotificationV1)
     */
    @AfterMapping
    default void insertReworkedStatus(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.insertReworkedStatus(bffFullNotificationV1);
    }

    /**
     * @see it.pagopa.pn.bff.utils.NotificationDetailUtility#cleanRelatedTimelineElements(BffFullNotificationV1)
     */
    @AfterMapping
    default void cleanRelatedTimelineElements(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.cleanRelatedTimelineElements(bffFullNotificationV1);
    }

    /**
     * @see NotificationDetailUtility#populateOtherDocuments(BffFullNotificationV1)
     */
    @AfterMapping
    default void populateOtherDocuments(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.populateOtherDocuments(bffFullNotificationV1);
    }

    /**
     * @see NotificationDetailUtility#checkRADDInTimeline(BffFullNotificationV1)
     */
    @AfterMapping
    default void checkRADDInTimeline(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.checkRADDInTimeline(bffFullNotificationV1);
    }

    /**
     * @see NotificationDetailUtility#insertCancelledStatusInTimeline(BffFullNotificationV1)
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
     * @see NotificationDetailUtility#populateMacroSteps(BffFullNotificationV1)
     */
    @AfterMapping
    default void populateMacroStep(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.populateMacroSteps(bffFullNotificationV1);
    }

    /**
     * @see NotificationDetailUtility#setReworkedStatusOnSteps(BffFullNotificationV1)
     */
    @AfterMapping
    default void setReworkedStatusOnSteps(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.setReworkedStatusOnSteps(bffFullNotificationV1);
    }

    /**
     * @see NotificationDetailUtility#sortNotificationStatusHistory(BffFullNotificationV1)
     */
    @AfterMapping
    default void sortNotificationStatusHistory(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.sortNotificationStatusHistory(bffFullNotificationV1);
    }
}