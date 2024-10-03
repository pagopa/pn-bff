package it.pagopa.pn.bff.mappers.payments;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the BffPaymentRequest to the ext-registry PaymentRequest and
 * the ext-registry PaymentResponse to the BffPaymentResponse
 */
@Mapper
public interface PaymentsCartMapper {
    PaymentsCartMapper modelMapper = Mappers.getMapper(PaymentsCartMapper.class);

    /**
     * Maps a BffPaymentRequest to an ext-registry PaymentRequest
     *
     * @param request the BffPaymentRequest to map
     * @return the mapped PaymentRequest
     */
    PaymentRequest mapPaymentRequest(BffPaymentRequest request);

    /**
     * Maps an ext-registry PaymentResponse to a BffPaymentResponse
     *
     * @param response the ext-registry PaymentResponse to map
     * @return the mapped BffPaymentResponse
     */
    BffPaymentResponse mapPaymentResponse(it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentResponse response);
}