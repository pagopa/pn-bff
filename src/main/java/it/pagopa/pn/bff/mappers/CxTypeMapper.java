package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.CxTypeAuthFleet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CxTypeMapper {

    CxTypeMapper cxTypeMapper = Mappers.getMapper(CxTypeMapper.class);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_recipient.model.CxTypeAuthFleet convertRecipientCXType(CxTypeAuthFleet cxType);

    it.pagopa.pn.bff.generated.openapi.msclient.delivery_pa.model.CxTypeAuthFleet convertPACXType(CxTypeAuthFleet cxType);

    it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.CxTypeAuthFleet convertExternalRegistriesCXType(CxTypeAuthFleet cxType);
}
