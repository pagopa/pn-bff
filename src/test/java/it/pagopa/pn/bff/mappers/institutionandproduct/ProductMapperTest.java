package it.pagopa.pn.bff.mappers.institutionandproduct;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductMapperTest {
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
    @Test
    void testBffInstitutionProductMapper() {
        ProductResourcePN productResourcePN = institutionAndProductMock.getProductResourcePNSMock().get(0);

        BffInstitutionProduct bffInstitutionProduct = ProductMapper.PRODUCT_MAPPER.toBffInstitutionProduct(productResourcePN);
        assertNotNull(bffInstitutionProduct);
        assertEquals(bffInstitutionProduct.getId(), productResourcePN.getId());
        assertEquals(bffInstitutionProduct.getTitle(), productResourcePN.getTitle());

        BffInstitutionProduct bffInstitutionProductNull = ProductMapper.PRODUCT_MAPPER.toBffInstitutionProduct(null);
        assertNull(bffInstitutionProductNull);
    }
}
