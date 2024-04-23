package it.pagopa.pn.bff.mappers.tosprivacy;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

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
}