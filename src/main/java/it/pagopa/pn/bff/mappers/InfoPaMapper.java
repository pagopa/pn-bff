package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface InfoPaMapper {
    InfoPaMapper infoPaMapper = Mappers.getMapper(InfoPaMapper.class);

    @Mapping(source = "description", target = "name")
    @Mapping(source = "userProductRoles", target = "productRole", qualifiedByName = "mapRoles")
    @Mapping(source = "rootParent.description", target = "parentName")
    BffInstitution toBffInstitution(InstitutionResourcePN institutionResourcePN);

    /**
     * Maps the roles by taking the first one
     *
     * @param roles The roles to map
     * @return The first role
     */
    @Named("mapRoles")
    default String mapRoles(List<String> roles) {
        if (roles != null && !roles.isEmpty()) {
            return roles.get(0);
        }
        return null;
    }

    BffInstitutionProduct toBffInstitutionProduct(ProductResourcePN productResourcePN);
}
