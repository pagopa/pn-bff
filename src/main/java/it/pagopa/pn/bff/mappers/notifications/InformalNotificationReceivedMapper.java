package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.InformalNotificationStatusHistoryElementV1;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.InformalNotificationStatusV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullInformalNotificationV1;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the FullReceivedInformalNotificationV1
 * to the BffFullInformalNotificationV1
 */
@Mapper
public interface InformalNotificationReceivedMapper {

    InformalNotificationReceivedMapper modelMapper = Mappers.getMapper(InformalNotificationReceivedMapper.class);

    /**
     * Maps a FullReceivedInformalNotificationV1 to a BffFullInformalNotificationV1
     *
     * @param notification the FullReceivedInformalNotificationV1 to map
     * @return the mapped BffFullInformalNotificationV1
     */
    @Mapping(target = "filedAt", ignore = true) // valued in setFiledAt (@AfterMapping)
    BffFullInformalNotificationV1 mapReceivedInformalNotificationDetail(FullReceivedInformalNotificationV1 notification);

    /**
     * Sets the filedAt field with the acceptance date of the notification, i.e. the activeFrom of the
     * notification status history element whose status is ACCEPTED. There is no dedicated field for it
     * in the upstream model, so it must be derived.
     *
     * @param source the source FullReceivedInformalNotificationV1
     * @param target the mapped BffFullInformalNotificationV1
     */
    @AfterMapping
    default void setFiledAt(FullReceivedInformalNotificationV1 source,
                            @MappingTarget BffFullInformalNotificationV1 target) {
        if (source.getNotificationStatusHistory() == null) {
            return;
        }
        source.getNotificationStatusHistory().stream()
                .filter(el -> el.getStatus() == InformalNotificationStatusV1.ACCEPTED)
                .map(InformalNotificationStatusHistoryElementV1::getActiveFrom)
                .findFirst()
                .ifPresent(target::setFiledAt);
    }
}
