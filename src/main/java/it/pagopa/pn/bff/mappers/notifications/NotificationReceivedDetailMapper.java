package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_push_rework.model.ReworkItem;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV28;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementCategoryV28;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementV28;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationDetailTimeline;
import it.pagopa.pn.bff.utils.NotificationDetailUtility;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

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
     * @param reworkItems  the rework items retrieved from pn-delivery-push (for correction type resolution)
     * @return the mapped BffFullNotificationV1
     */
    @Mapping(target = "filedAt", ignore = true) // valued in setFiledAt (@AfterMapping)
    BffFullNotificationV1 mapReceivedNotificationDetail(FullReceivedNotificationV28 notification,
                                                        @Context List<ReworkItem> reworkItems);

    /**
     * Sets the filedAt field with the acceptance date of the notification, i.e. the timestamp of the
     * timeline element whose category is REQUEST_ACCEPTED. There is no dedicated field for it in the
     * upstream model, so it must be derived.
     *
     * @param source the source FullReceivedNotificationV28
     * @param target the mapped BffFullNotificationV1
     */
    @AfterMapping
    default void setFiledAt(FullReceivedNotificationV28 source,
                            @MappingTarget BffFullNotificationV1 target) {
        if (source.getTimeline() == null) {
            return;
        }
        source.getTimeline().stream()
                .filter(el -> el.getCategory() == TimelineElementCategoryV28.REQUEST_ACCEPTED)
                .map(TimelineElementV28::getTimestamp)
                .findFirst()
                .ifPresent(target::setFiledAt);
    }

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
    default void insertReworkedStatus(@MappingTarget BffFullNotificationV1 bffFullNotificationV1,
                                      @Context List<ReworkItem> reworkItems) {
        NotificationDetailUtility.insertReworkedStatus(bffFullNotificationV1, reworkItems);
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
     * @see NotificationDetailUtility#setAarDocumentAvailability(BffFullNotificationV1)
     */
    @AfterMapping
    default void setReworkedStatusOnSteps(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.setReworkedStatusOnSteps(bffFullNotificationV1);
        NotificationDetailUtility.setAarDocumentAvailability(bffFullNotificationV1);
    }

    /**
     * @see NotificationDetailUtility#sortNotificationStatusHistory(BffFullNotificationV1)
     */
    @AfterMapping
    default void sortNotificationStatusHistory(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.sortNotificationStatusHistory(bffFullNotificationV1);
    }
}