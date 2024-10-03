package it.pagopa.pn.bff.mappers.notifications;

import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.RequestCheckAarMandateDto;
import it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.ResponseCheckAarMandateDto;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCheckAarRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffCheckAarResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the parameters (from bff version to ms version and viceversa)
 * used to call the AArQrCodeCheck api
 */
@Mapper
public interface NotificationAarQrCodeMapper {
    // Instance of the mapper
    NotificationAarQrCodeMapper modelMapper = Mappers.getMapper(NotificationAarQrCodeMapper.class);

    /**
     * Maps a BffCheckAarMandate to a RequestCheckAarMandateDto
     *
     * @param requestCheckAarMandate the BffCheckAarRequest to map
     * @return the mapped RequestCheckAarMandateDto
     */
    RequestCheckAarMandateDto toRequestCheckAarMandateDto(BffCheckAarRequest requestCheckAarMandate);

    /**
     * Maps a ResponseCheckAarMandate to a BffCheckAarMandateDto
     *
     * @param responseCheckAarMandate the ResponseCheckAarMandateDto to map
     * @return the mapped BffCheckAarResponse
     */
    BffCheckAarResponse toBffResponseCheckAarMandateDto(ResponseCheckAarMandateDto responseCheckAarMandate);
}