package it.pagopa.pn.bff.mappers.tosprivacy;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction;
import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentType;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the Consent to the BffConsent
 */
@Mapper
public interface TosPrivacyConsentMapper {
    TosPrivacyConsentMapper tosPrivacyConsentMapper = Mappers.getMapper(TosPrivacyConsentMapper.class);

    /**
     * Maps a Consent to a BffConsent
     *
     * @param consent the Consent to map
     * @return the mapped BffConsent
     */
    BffConsent mapConsent(Consent consent);

    /**
     * Maps a bff ConsentAction to a microservice ConsentAction
     *
     * @param action the bff ConsentAction to map
     * @return the mapped microservice ConsentAction
     */
    ConsentAction.ActionEnum convertConsentAction(BffTosPrivacyActionBody.ActionEnum action);

    /**
     * Maps a bff ConsentType to a microservice ConsentType
     *
     * @param type the bff ConsentType to map
     * @return the mapped microservice ConsentType
     */
    ConsentType convertConsentType(it.pagopa.pn.bff.generated.openapi.server.v1.dto.ConsentType type);
}