package it.pagopa.pn.bff.mappers.tosprivacy;

import it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.Consent;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffConsent;
import org.mapstruct.Mapper;
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
}
