package it.pagopa.pn.bff.mappers.payments;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoV21;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentInfoItem;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.PaymentInfoRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapstruct mapper interface, used to map the bff PaymentInfoRequest to the ext-registry PaymentInfoRequest and
 * the PaymentInfoV21 to the BffPaymentInfoResponse
 */
@Mapper
public interface PaymentsInfoMapper {
    PaymentsInfoMapper modelMapper = Mappers.getMapper(PaymentsInfoMapper.class);

    /**
     * Maps a bff PaymentInfoRequest to an ext-registry PaymentInfoRequest
     *
     * @param request the bff PaymentInfoRequest to map
     * @return the mapped PaymentInfoRequest
     */
    List<it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentInfoRequest> mapPaymentInfoRequest(List<PaymentInfoRequest> request);

    /**
     * Maps a PaymentInfoV21 to a BffPaymentInfoResponse
     *
     * @param response the PaymentInfoV21 to map
     * @return the mapped BffPaymentInfoResponse
     */
    BffPaymentInfoItem mapPaymentInfoResponse(PaymentInfoV21 response);
}