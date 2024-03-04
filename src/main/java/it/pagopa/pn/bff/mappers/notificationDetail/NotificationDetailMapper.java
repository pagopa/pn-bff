package it.pagopa.pn.bff.mappers.notificationDetail;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationDetailTimeline;
import it.pagopa.pn.bff.utils.notificationDetail.NotificationDetailUtility;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {RecipientNotificationTimelineMapper.class, SenderNotificationTimelineMapper.class})
public interface NotificationDetailMapper {

    // Instance of the mapper
    NotificationDetailMapper modelMapper = Mappers.getMapper(NotificationDetailMapper.class);

    /**
     * Maps a FullReceivedNotificationV23 to a BffFullNotificationV1
     *
     * @param notification the FullReceivedNotificationV23 to map
     * @return the mapped BffFullNotificationV1
     */
    BffFullNotificationV1 mapReceivedNotificationDetail(FullReceivedNotificationV23 notification);

    /**
     * Maps a FullSentNotificationV23 to a BffFullNotificationV1
     *
     * @param notification the FullSentNotificationV23 to map
     * @return the mapped BffFullNotificationV1
     */
    BffFullNotificationV1 mapSentNotificationDetail(FullSentNotificationV23 notification);


    @AfterMapping
    default void populateOtherDocuments(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.populateOtherDocuments(bffFullNotificationV1);
    }

    @AfterMapping
    default void checkRADDInTimeline(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.checkRADDInTimeline(bffFullNotificationV1);
    }

    @AfterMapping
    default void insertCancelledStatusInTimeline(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.insertCancelledStatusInTimeline(bffFullNotificationV1);
    }

    @AfterMapping
    default void setTimelineIndexAndHidden(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        for (int i = 0; i < bffFullNotificationV1.getTimeline().size(); i++) {
            NotificationDetailTimeline timelineElement = bffFullNotificationV1.getTimeline().get(i);
            timelineElement.setIndex(i);
            timelineElement.setHidden(!NotificationDetailUtility.timelineElementMustBeShown(timelineElement));
        }
    }

    @AfterMapping
    default void populateMacroStep(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.populateMacroSteps(bffFullNotificationV1);
    }

    @AfterMapping
    default void sortNotificationStatusHistory(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        bffFullNotificationV1.getNotificationStatusHistory().sort((o1, o2) ->
                o2.getActiveFrom().toInstant().toEpochMilli() >= o1.getActiveFrom().toInstant().toEpochMilli() ? 1 : -1
        );
    }
}