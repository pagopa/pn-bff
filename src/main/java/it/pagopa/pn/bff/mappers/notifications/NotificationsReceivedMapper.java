package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.NotificationSearchResponse;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.RequestCheckAarMandateDto;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.ResponseCheckAarMandateDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffNotificationsResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffRequestCheckAarMandateDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffResponseCheckAarMandateDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the NotificationSearchResponse
 * to the BffNotificationsResponse
 */
@Mapper
public interface NotificationsReceivedMapper {
    NotificationsReceivedMapper modelMapper = Mappers.getMapper(NotificationsReceivedMapper.class);

    /**
     * Maps a NotificationSearchResponse to a BffNotificationsResponse
     *
     * @param notificationSearchResponse the NotificationSearchResponse to map
     * @return the mapped BffNotificationsResponse
     */
    BffNotificationsResponse toBffNotificationsResponse(NotificationSearchResponse notificationSearchResponse);

    /**
     * Maps a BffRequestCheckAarMandateDto to a RequestCheckAarMandateDto
     *
     * @param requestCheckAarMandateDto the BffRequestCheckAarMandateDto to map
     * @return the mapped RequestCheckAarMandateDto
     */
    RequestCheckAarMandateDto toRequestCheckAarMandateDto(BffRequestCheckAarMandateDto requestCheckAarMandateDto);

    /**
     * Maps a ResponseCheckAarMandateDto to a BffResponseCheckAarMandateDto
     *
     * @param responseCheckAarMandateDto the ResponseCheckAarMandateDto to map
     * @return the mapped BffResponseCheckAarMandateDto
     */
    BffResponseCheckAarMandateDto toBffResponseCheckAarMandateDto(ResponseCheckAarMandateDto responseCheckAarMandateDto);
}