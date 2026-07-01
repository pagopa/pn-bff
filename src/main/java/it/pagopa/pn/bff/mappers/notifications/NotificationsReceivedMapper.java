package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CommunicationOutcomes;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.FullNotificationSearchRow;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.LegalNotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.UnifiedNotificationStatus;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationSearchRow;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffLegalNotificationsResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the FullNotificationSearchResponse
 * to the BffFullNotificationsResponse
 */
@Mapper
public interface NotificationsReceivedMapper {
    NotificationsReceivedMapper modelMapper = Mappers.getMapper(NotificationsReceivedMapper.class);

    /**
     * Maps a FullNotificationSearchResponse to a BffFullNotificationsResponse
     *
     * @param fullNotificationSearchResponse the FullNotificationSearchResponse to map
     * @return the mapped BffFullNotificationsResponse
     */
    BffFullNotificationsResponse toBffFullNotificationsResponse(FullNotificationSearchResponse fullNotificationSearchResponse);

    /**
     * Maps a LegalNotificationSearchResponse to a BffLegalNotificationsResponse
     *
     * @param legalNotificationSearchResponse the LegalNotificationSearchResponse to map
     * @return the mapped BffLegalNotificationsResponse
     */
    BffLegalNotificationsResponse toBffLegalNotificationsResponse(LegalNotificationSearchResponse legalNotificationSearchResponse);

    /**
     * Sets the isNewNotification flag (true if the notification has not been viewed yet) on each mapped row.
     */
    @AfterMapping
    default void computeIsNewNotification(FullNotificationSearchRow row, @MappingTarget BffFullNotificationSearchRow target) {
        CommunicationOutcomes outcomes = row.getCommunicationOutcomes();
        if (outcomes != null && outcomes.getViewed() != null) {
            target.setIsNewNotification(!outcomes.getViewed());
            return;
        }
        UnifiedNotificationStatus status = row.getNotificationStatus();
        target.setIsNewNotification(
                status != UnifiedNotificationStatus.VIEWED
                        && status != UnifiedNotificationStatus.CANCELLED
                        && status != UnifiedNotificationStatus.RETURNED_TO_SENDER
                        && status != UnifiedNotificationStatus.EFFECTIVE_DATE
        );
    }
}