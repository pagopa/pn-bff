package it.pagopa.pn.bff.mappers.institutionandproduct;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapstruct mapper interface, used to map the ProductResourcePN to the BffInstitutionProduct
 */
@Mapper
public interface ProductMapper {
    ProductMapper PRODUCT_MAPPER = Mappers.getMapper(ProductMapper.class);

    /**
     * Maps a product resource to a BffInstitutionProduct
     *
     * @param productResourcePN The product resource to map
     * @return The mapped BffInstitutionProduct
     */
    BffInstitutionProduct toBffInstitutionProduct(ProductResourcePN productResourcePN);
}