package it.pagopa.pn.bff.mappers.payments;

import it.pagopa.pn.bff.generated.openapi.msclient.emd.model.PaymentUrlResponse;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentTppResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentsTppMapper {
    PaymentsTppMapper modelMapper = Mappers.getMapper(PaymentsTppMapper.class);

    BffPaymentTppResponse mapPaymentTppResponse(PaymentUrlResponse response);
}