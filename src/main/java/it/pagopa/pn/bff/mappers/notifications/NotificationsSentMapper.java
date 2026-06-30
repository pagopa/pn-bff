package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.LegalNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffLegalNotificationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the LegalNotificationSearchResponse
 * to the BffLegalNotificationsResponse
 */
@Mapper
public interface NotificationsSentMapper {
    NotificationsSentMapper modelMapper = Mappers.getMapper(NotificationsSentMapper.class);

    /**
     * Maps a LegalNotificationSearchResponse to a BffLegalNotificationsResponse
     *
     * @param legalNotificationSearchResponse the LegalNotificationSearchResponse to map
     * @return the mapped BffLegalNotificationsResponse
     */
    BffLegalNotificationsResponse toBffLegalNotificationsResponse(LegalNotificationSearchResponse legalNotificationSearchResponse);

}