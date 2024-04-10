package it.pagopa.pn.bff.mappers;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries.model.RootParentResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InfoPaMapperTest {
    @Test
    void testBffInstitutionMapper() {
        InstitutionResourcePN institutionResourcePN = new InstitutionResourcePN();
        institutionResourcePN.setRootParent(new RootParentResourcePN());
        institutionResourcePN.getRootParent().setDescription("Parent Description");

        BffInstitution bffInstitution = InfoPaMapper.infoPaMapper.toBffInstitution(institutionResourcePN);
        assertNotNull(bffInstitution);
        assertEquals("Parent Description", bffInstitution.getParentName());

        BffInstitution bffInstitutionNull = InfoPaMapper.infoPaMapper.toBffInstitution(null);
        assertNull(bffInstitutionNull);
    }

    @Test
    void testBffInstitutionProductMapper() {
        ProductResourcePN productResourcePN = new ProductResourcePN();

        BffInstitutionProduct bffInstitutionProduct = InfoPaMapper.infoPaMapper.toBffInstitutionProduct(productResourcePN);
        assertNotNull(bffInstitutionProduct);

        BffInstitutionProduct bffInstitutionProductNull = InfoPaMapper.infoPaMapper.toBffInstitutionProduct(null);
        assertNull(bffInstitutionProductNull);
    }
}
