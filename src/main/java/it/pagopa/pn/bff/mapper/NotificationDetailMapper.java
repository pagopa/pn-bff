package it.pagopa.pn.bff.mapper;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.FullSentNotificationV23;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedNotificationV23;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffFullNotificationV1;
import it.pagopa.pn.bff.mapper.NotificationElementMapper.RecipientNotificationTimelineMapper;
import it.pagopa.pn.bff.mapper.NotificationElementMapper.SenderNotificationTimelineMapper;
import it.pagopa.pn.bff.utils.notificationDetail.NotificationDetailUtility;
import it.pagopa.pn.bff.utils.notificationDetail.NotificationMacroStepPopulator;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {DateMapper.class, RecipientNotificationTimelineMapper.class, SenderNotificationTimelineMapper.class})
public interface NotificationDetailMapper {

    NotificationDetailMapper modelMapper = Mappers.getMapper(NotificationDetailMapper.class);

    BffFullNotificationV1 mapNotificationDetail(FullReceivedNotificationV23 notification);

    BffFullNotificationV1 mapSentNotificationDetail(FullSentNotificationV23 notification);


    @AfterMapping
    default void populateOtherDocuments(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.populateOtherDocuments(bffFullNotificationV1);
    }

    @AfterMapping
    default void insertCancelledStatusInTimeline(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationDetailUtility.insertCancelledStatusInTimeline(bffFullNotificationV1);
    }

    @AfterMapping
    default void setTimelineIndex(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        for (int i = 0; i < bffFullNotificationV1.getTimeline().size(); i++) {
            bffFullNotificationV1.getTimeline().get(i).setIndex(i);
        }
    }

    @AfterMapping
    default void populateMacroStep(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        NotificationMacroStepPopulator.populateMacroSteps(bffFullNotificationV1);
    }

    @AfterMapping
    default void sortNotificationStatusHistory(@MappingTarget BffFullNotificationV1 bffFullNotificationV1) {
        bffFullNotificationV1.getNotificationStatusHistory().sort((o1, o2) -> o2.getActiveFrom().compareTo(o1.getActiveFrom()));
    }
}