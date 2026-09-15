package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_informal_pa_b2b.model.FullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullSentInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNotificationChannelType;
import it.pagopa.pn.bff.utils.SentInformalNotificationChannelStatusResolver;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface InformalNotificationSentMapper {

    InformalNotificationSentMapper modelMapper = Mappers.getMapper(InformalNotificationSentMapper.class);

    @Mapping(target = "channelsStatus", ignore = true)
    BffFullSentInformalNotificationV1 mapSentInformalNotificationDetail(
            FullSentInformalNotificationV1 notification,
            List<BffNotificationChannelType> channels
    );

    @AfterMapping
    default void setChannelsStatus(
            FullSentInformalNotificationV1 notification,
            List<BffNotificationChannelType> channels,
            @MappingTarget BffFullSentInformalNotificationV1 target
    ) {
        target.setChannelsStatus(
                SentInformalNotificationChannelStatusResolver.populateChannelStatuses(
                        notification.getNotificationStatus(),
                        notification.getTimeline(),
                        channels
                )
        );
    }
}