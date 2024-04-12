package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.RootParentResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InstitutionProductMapperTest {
    @Test
    void testBffInstitutionMapper() {
        InstitutionResourcePN institutionResourcePN = new InstitutionResourcePN();
        institutionResourcePN.setRootParent(new RootParentResourcePN());
        institutionResourcePN.getRootParent().setDescription("Parent Description");

        BffInstitution bffInstitution = it.pagopa.pn.bff.mappers.InstitutionProductMapper.institutionProductMapper.toBffInstitution(institutionResourcePN);
        assertNotNull(bffInstitution);
        assertEquals("Parent Description", bffInstitution.getParentName());

        BffInstitution bffInstitutionNull = it.pagopa.pn.bff.mappers.InstitutionProductMapper.institutionProductMapper.toBffInstitution(null);
        assertNull(bffInstitutionNull);
    }

    @Test
    void testBffInstitutionProductMapper() {
        ProductResourcePN productResourcePN = new ProductResourcePN();

        BffInstitutionProduct bffInstitutionProduct = it.pagopa.pn.bff.mappers.InstitutionProductMapper.institutionProductMapper.toBffInstitutionProduct(productResourcePN);
        assertNotNull(bffInstitutionProduct);

        BffInstitutionProduct bffInstitutionProductNull = it.pagopa.pn.bff.mappers.InstitutionProductMapper.institutionProductMapper.toBffInstitutionProduct(null);
        assertNull(bffInstitutionProductNull);
    }
}
