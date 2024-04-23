package it.pagopa.pn.bff.mappers.tosprivacy;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffTosPrivacyActionBody.ActionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TosPrivacyConsentActionMapper {
    TosPrivacyConsentActionMapper tosPrivacyConsentActionMapper = Mappers.getMapper(TosPrivacyConsentActionMapper.class);

    /**
     * Maps a ConsentAction to a ActionEnum
     *
     * @param consent the ConsentAction to map
     * @return the mapped ActionEnum
     */
    @ValueMapping(source = "DECLINE", target = "DECLINE")
    @ValueMapping(source = "ACCEPT", target = "ACCEPT")
    it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.ConsentAction.ActionEnum convertConsentAction(ActionEnum consent);
}