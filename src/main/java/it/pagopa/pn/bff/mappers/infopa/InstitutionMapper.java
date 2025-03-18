package it.pagopa.pn.bff.mappers.infopa;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.BffInstitution;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapstruct mapper interface, used to map the InstitutionResourcePN to the BffInstitution
 */
@Mapper
public interface InstitutionMapper {
    InstitutionMapper modelMapper = Mappers.getMapper(InstitutionMapper.class);

    String pathTokenExchange = "/token-exchange?institutionId=";
    String pathProdId = "&productId=";
    String pathInstitutions = "/institutions/";
    String pathLogoName = "/logo.png";

    /**
     * Maps an institution resource to a BffInstitution
     *
     * @param institutionResourcePN The institution resource to map
     * @return The mapped BffInstitution
     */
    @Mapping(source = "description", target = "name")
    @Mapping(source = "userProductRoles", target = "productRole", qualifiedByName = "mapRoles")
    @Mapping(source = "rootParent.description", target = "parentName")
    @Mapping(source = "id", target = "entityUrl", qualifiedByName = "mapEntityUrl")
    BffInstitution toBffInstitution(InstitutionResourcePN institutionResourcePN, @Context PnBffConfigs pnBffConfigs);

    /**
     * Maps the roles by taking the first one
     *
     * @param roles The roles to map
     * @return The first role
     */
    @Named("mapRoles")
    default String mapRoles(List<String> roles) {
        return roles != null && !roles.isEmpty() ? roles.get(0) : "";
    }

    /**
     * Compose the entity url
     *
     * @param id           the institution id
     * @param pnBffConfigs the spring configuration
     * @return the entity url
     */
    @Named("mapEntityUrl")
    default String mapEntityUrl(String id, @Context PnBffConfigs pnBffConfigs) {
        return pnBffConfigs.getSelfcareBaseUrl() + pathTokenExchange + id + pathProdId + pnBffConfigs.getSelfcareSendProdId();
    }

    /**
     * Compose the logo URL of the entity
     *
     * @param bffInstitution the BffFullNotificationV1 to map
     */
    @AfterMapping
    default void setLogoUrl(@MappingTarget BffInstitution bffInstitution, @Context PnBffConfigs pnBffConfigs) {
        bffInstitution.setLogoUrl(pnBffConfigs.getSelfcareCdnUrl() + pathInstitutions + bffInstitution.getId() + pathLogoName);
    }

}