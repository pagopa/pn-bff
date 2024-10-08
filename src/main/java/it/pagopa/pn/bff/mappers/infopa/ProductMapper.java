package it.pagopa.pn.bff.mappers.infopa;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.user_info.BffInstitutionProduct;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the ProductResourcePN to the BffInstitutionProduct
 */
@Mapper
public interface ProductMapper {
    ProductMapper modelMapper = Mappers.getMapper(ProductMapper.class);

    String pathTokenExchange = "/token-exchange?institutionId=";
    String pathProdId = "&productId=";

    /**
     * Maps a product resource to a BffInstitutionProduct
     *
     * @param productResourcePN The product resource to map
     * @return The mapped BffInstitutionProduct
     */
    @Mapping(source = "id", target = "productUrl", qualifiedByName = "mapProductUrl")
    BffInstitutionProduct toBffInstitutionProduct(ProductResourcePN productResourcePN, @Context PnBffConfigs pnBffConfigs, @Context String institutionId);

    /**
     * Compose the product url
     *
     * @param id            the product id
     * @param pnBffConfigs  the spring configuration
     * @param institutionId the institution id
     * @return the product url
     */
    @Named("mapProductUrl")
    default String mapProductUrl(String id, @Context PnBffConfigs pnBffConfigs, @Context String institutionId) {
        return pnBffConfigs.getSelfcareBaseUrl() + pathTokenExchange + institutionId + pathProdId + id;
    }

}