package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the bff CxTypeAuthFleet
 * to the microservice CxTypeAuthFleet
 */
@Mapper
public interface CxTypeMapper {

    CxTypeMapper cxTypeMapper = Mappers.getMapper(CxTypeMapper.class);

    /**
     * Map bff CxTypeAuthFleet to the delivery recipient CxTypeAuthFleet
     *
     * @param cxType bff CxTypeAuthFleet
     * @return the mapped CxTypeAuthFleet
     */
    @ValueMapping(source = "RADD", target = MappingConstants.NULL)
    it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet convertDeliveryRecipientCXType(CxTypeAuthFleet cxType);

    /**
     * Map bff CxTypeAuthFleet to the delivery b2b pa CxTypeAuthFleet
     *
     * @param cxType bff CxTypeAuthFleet
     * @return the mapped CxTypeAuthFleet
     */
    @ValueMapping(source = "RADD", target = MappingConstants.NULL)
    it.pagopa.pn.bff.generated.openapi.msclient.delivery_b2b_pa.model.CxTypeAuthFleet convertDeliveryB2bPACXType(CxTypeAuthFleet cxType);

    /**
     * Map bff CxTypeAuthFleet to the delivery web pa CxTypeAuthFleet
     *
     * @param cxType bff CxTypeAuthFleet
     * @return the mapped CxTypeAuthFleet
     */
    @ValueMapping(source = "RADD", target = MappingConstants.NULL)
    it.pagopa.pn.bff.generated.openapi.msclient.delivery_web_pa.model.CxTypeAuthFleet convertDeliveryWebPACXType(CxTypeAuthFleet cxType);

    /**
     * Map bff CxTypeAuthFleet to the api key CxTypeAuthFleet
     *
     * @param cxType bff CxTypeAuthFleet
     * @return the mapped CxTypeAuthFleet
     */
    @ValueMapping(source = "RADD", target = MappingConstants.NULL)
    it.pagopa.pn.bff.generated.openapi.msclient.apikey_pa.model.CxTypeAuthFleet convertApiKeysPACXType(CxTypeAuthFleet cxType);

    /**
     * Map bff CxTypeAuthFleet to the external registries selfcare CxTypeAuthFleet
     *
     * @param cxType bff CxTypeAuthFleet
     * @return the mapped CxTypeAuthFleet
     */
    @ValueMapping(source = "RADD", target = MappingConstants.NULL)
    it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.CxTypeAuthFleet convertExternalRegistriesCXType(CxTypeAuthFleet cxType);

    /**
     * Map bff CxTypeAuthFleet to the user attributes CxTypeAuthFleet
     *
     * @param cxType bff CxTypeAuthFleet
     * @return the mapped CxTypeAuthFleet
     */
    @ValueMapping(source = "RADD", target = MappingConstants.NULL)
    it.pagopa.pn.bff.generated.openapi.msclient.user_attributes.model.CxTypeAuthFleet convertUserAttributesCXType(CxTypeAuthFleet cxType);

    /**
     * Map bff CxTypeAuthFleet to the delivery push CxTypeAuthFleet
     *
     * @param cxType bff CxTypeAuthFleet
     * @return the mapped CxTypeAuthFleet
     */
    @ValueMapping(source = "RADD", target = MappingConstants.NULL)
    it.pagopa.pn.bff.generated.openapi.msclient.delivery_push.model.CxTypeAuthFleet convertDeliveryPushCXType(CxTypeAuthFleet cxType);

    /**
     * Map bff CxTypeAuthFleet to the mandate CxTypeAuthFleet
     *
     * @param cxType bff CxTypeAuthFleet
     * @return the mapped CxTypeAuthFleet
     */
    @ValueMapping(source = "RADD", target = MappingConstants.NULL)
    it.pagopa.pn.bff.generated.openapi.msclient.mandate.model.CxTypeAuthFleet convertMandateCXType(CxTypeAuthFleet cxType);
}