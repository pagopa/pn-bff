package it.pagopa.pn.bff.mappers.tosprivacy;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.TosPrivacyActionBody.ActionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TosPrivacyMapper {
    TosPrivacyMapper tosPrivacyMapper = Mappers.getMapper(TosPrivacyMapper.class);

    /**
     * Maps a Consent to a BffConsent
     *
     * @param consent the Consent to map
     * @return the mapped BffConsent
     */
    public BffConsent mapTosPrivacyConsent(Consent consent);

    // This mapper should return From "DECLINE" a ConsentAction Object like {"action":"DECLINE"}
//    public default ConsentAction convertConsentAction(ActionEnum consent) {
//        return new ConsentAction().action(
//                it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction.ActionEnum.fromValue(consent.getValue()));
//    }

    @ValueMapping(source = "DECLINE", target = "DECLINE")
    @ValueMapping(source = "ACCEPT", target = "ACCEPT")
    public it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction.ActionEnum convertConsentAction(ActionEnum consent);
}
