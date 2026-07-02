package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.*;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationSearchRow;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffFullNotificationsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffLegalNotificationsResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.EnumSet;
import java.util.Set;

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
        target.setIsNewNotification(isNewNotification(row));
    }

    default boolean isNewNotification(FullNotificationSearchRow row) {
        FullNotificationSearchRow.CommunicationTypeEnum communicationType = row.getCommunicationType();

        Set<UnifiedNotificationStatus> NOT_NEW_LEGAL_STATUSES = EnumSet.of(
                UnifiedNotificationStatus.VIEWED,
                UnifiedNotificationStatus.CANCELLED,
                UnifiedNotificationStatus.RETURNED_TO_SENDER,
                UnifiedNotificationStatus.EFFECTIVE_DATE
        );

        if (communicationType == FullNotificationSearchRow.CommunicationTypeEnum.LEGAL) {
            return !NOT_NEW_LEGAL_STATUSES.contains(row.getNotificationStatus());
        }

        if (communicationType == FullNotificationSearchRow.CommunicationTypeEnum.INFORMAL) {
            CommunicationOutcomes outcomes = row.getCommunicationOutcomes();
            return outcomes != null
                    && outcomes.getViewed() != null
                    && !outcomes.getViewed();
        }

        return false;
    }
}