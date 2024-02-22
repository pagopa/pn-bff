package it.pagopa.pn.bff.mapper.NotificationElementMapper;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.TimelineElementV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.NotificationDetailTimeline;
import it.pagopa.pn.bff.mapper.DateMapper;
import it.pagopa.pn.bff.utils.notificationDetail.NotificationDetailUtility;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {DateMapper.class})
public interface RecipientNotificationTimelineMapper {

    RecipientNotificationTimelineMapper recipientTimelineMapper = Mappers.getMapper(RecipientNotificationTimelineMapper.class);

    @Mapping(target = "elementId", source = "elementId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "legalFactsIds", source = "legalFactsIds")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "details", source = "details")
    @Mapping(target = "hidden", ignore = true)
    @Mapping(target = "index", ignore = true)
    public NotificationDetailTimeline mapTimelineElement(TimelineElementV23 timelineElement);

    @AfterMapping
    default void setHidden(@MappingTarget NotificationDetailTimeline notificationDetailTimeline) {
        notificationDetailTimeline.setHidden(!NotificationDetailUtility.timelineElementMustBeShown(notificationDetailTimeline));
    }

    @AfterMapping
    default void removeNotRefinedRecipientIndexes(@MappingTarget NotificationDetailTimeline notificationDetailTimeline) {
        if (notificationDetailTimeline.getDetails() != null) {
            notificationDetailTimeline.getDetails().setNotRefinedRecipientIndexes(null);
        }
    }
}
