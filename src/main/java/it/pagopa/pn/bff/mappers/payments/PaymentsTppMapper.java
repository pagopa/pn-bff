package it.pagopa.pn.bff.mappers.payments;

import it.pagopa.pn.bff.generated.openapi.msclient.emd.model.PaymentUrlResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentTppResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the PaymentUrlResponse to the BffPaymentTppResponse
 */
@Mapper
public interface PaymentsTppMapper {
    PaymentsTppMapper modelMapper = Mappers.getMapper(PaymentsTppMapper.class);

    /**
     * Maps a PaymentUrlResponse to a BffPaymentTppResponse
     *
     * @param response the PaymentUrlResponse to map
     * @return the mapped BffPaymentTppResponse
     */
    BffPaymentTppResponse mapPaymentTppResponse(PaymentUrlResponse response);
}