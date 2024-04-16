package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.RootParentResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.mappers.institutionandproduct.InstitutionMapper;
import it.pagopa.pn.bff.mappers.institutionandproduct.ProductMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InstitutionAndProductMapperTest {
    @Test
    void testBffInstitutionMapper() {
        InstitutionResourcePN institutionResourcePN = new InstitutionResourcePN();
        institutionResourcePN.setRootParent(new RootParentResourcePN());
        institutionResourcePN.getRootParent().setDescription("Parent Description");

        BffInstitution bffInstitution = InstitutionMapper.INSTITUTION_MAPPER.toBffInstitution(institutionResourcePN);
        assertNotNull(bffInstitution);
        assertEquals("Parent Description", bffInstitution.getParentName());

        BffInstitution bffInstitutionNull = InstitutionMapper.INSTITUTION_MAPPER.toBffInstitution(null);
        assertNull(bffInstitutionNull);
    }

    @Test
    void testBffInstitutionProductMapper() {
        ProductResourcePN productResourcePN = new ProductResourcePN();

        BffInstitutionProduct bffInstitutionProduct = ProductMapper.PRODUCT_MAPPER.toBffInstitutionProduct(productResourcePN);
        assertNotNull(bffInstitutionProduct);

        BffInstitutionProduct bffInstitutionProductNull = ProductMapper.PRODUCT_MAPPER.toBffInstitutionProduct(null);
        assertNull(bffInstitutionProductNull);
    }
}
