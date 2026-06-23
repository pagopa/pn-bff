package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullReceivedInformalNotificationV1;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullInformalNotificationV1;
import org.mapstruct.Mapper;
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
    BffFullInformalNotificationV1 mapReceivedInformalNotificationDetail(FullReceivedInformalNotificationV1 notification);
}
