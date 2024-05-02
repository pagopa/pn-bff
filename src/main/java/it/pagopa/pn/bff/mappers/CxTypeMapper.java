package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CxTypeMapper {

    CxTypeMapper cxTypeMapper = Mappers.getMapper(CxTypeMapper.class);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet convertDeliveryRecipientCXType(CxTypeAuthFleet cxType);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet convertDeliveryB2bPACXType(CxTypeAuthFleet cxType);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet convertDeliveryWebPACXType(CxTypeAuthFleet cxType);

    it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet convertApiKeysPACXType(CxTypeAuthFleet cxType);

    it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet convertExternalRegistriesCXType(CxTypeAuthFleet cxType);

    it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet convertUserAttributesCXType(CxTypeAuthFleet cxType);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet convertDeliveryPushCXType(CxTypeAuthFleet cxType);
}