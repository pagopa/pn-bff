package it.pagopa.pn.bff.mappers.institutionandproduct;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.RootParentResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InstitutionMapperTest {
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
    @Test
    void testBffInstitutionMapper() {
        InstitutionResourcePN institutionResourcePN = institutionAndProductMock.getInstitutionResourcePNSMock().get(0);

        institutionResourcePN.setRootParent(new RootParentResourcePN());
        institutionResourcePN.getRootParent().setDescription("Parent Description");

        BffInstitution bffInstitution = InstitutionMapper.INSTITUTION_MAPPER.toBffInstitution(institutionResourcePN);
        assertNotNull(bffInstitution);
        assertEquals(bffInstitution.getName(), institutionResourcePN.getDescription());
        assertEquals(bffInstitution.getProductRole(), institutionResourcePN.getUserProductRoles().get(0));
        assertEquals(bffInstitution.getParentName(), institutionResourcePN.getRootParent().getDescription());
        assertNull(bffInstitution.getLogoUrl());

        BffInstitution bffInstitutionNull = InstitutionMapper.INSTITUTION_MAPPER.toBffInstitution(null);
        assertNull(bffInstitutionNull);
    }
}
