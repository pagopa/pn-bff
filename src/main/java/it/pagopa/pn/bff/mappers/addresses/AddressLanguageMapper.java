package it.pagopa.pn.bff.mappers.addresses;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_payment_info.model.PaymentRequest;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxLanguage;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentRequest;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.notifications.BffPaymentResponse;
import it.pagopa.pn.bff.mappers.payments.PaymentsCartMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressLanguageMapper {
    AddressLanguageMapper modelMapper = Mappers.getMapper(AddressLanguageMapper.class);

    /**
     * Maps a CxLanguage to a string language
     *
     * @param language the BffPaymentRequest to map
     * @return the mapped PaymentRequest
     */
    default CxLanguage mapAddressesLanguage(String language) {
        if (language == null) {
            return CxLanguage.IT;
        }
        try {
            return CxLanguage.valueOf(language.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return CxLanguage.IT;
        }
    }

}
