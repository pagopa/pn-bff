package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NewNotificationRequestV25;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.NewNotificationResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNewNotificationRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffNewNotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffNewNotificationRequest to the NewNotificationRequestV25,
 * and the NewNotificationResponse to the BffNewNotificationResponse
 */
@Mapper
public interface NewSentNotificationMapper {
    // Instance of the mapper
    NewSentNotificationMapper modelMapper = Mappers.getMapper(NewSentNotificationMapper.class);

    /**
     * Maps a BffNewNotificationRequest to a NewNotificationRequestV25
     *
     * @param request the BffNewNotificationRequest to map
     * @return the mapped NewNotificationRequestV25
     */
    NewNotificationRequestV25 mapRequest(BffNewNotificationRequest request);

    /**
     * Maps a NewNotificationResponse to a BffNewNotificationResponse
     *
     * @param response the NewNotificationResponse to map
     * @return the mapped BffNewNotificationResponse
     */
    BffNewNotificationResponse mapResponse(NewNotificationResponse response);
}