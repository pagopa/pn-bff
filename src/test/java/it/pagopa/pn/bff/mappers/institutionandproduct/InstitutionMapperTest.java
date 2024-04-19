package it.pagopa.pn.bff.mappers.institutionandproduct;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.RootParentResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PnBffConfigs.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class InstitutionMapperTest {
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
    @Autowired
    private PnBffConfigs pnBffConfigs;

    @Test
    void testBffInstitutionMapper() {
        InstitutionResourcePN institutionResourcePN = institutionAndProductMock.getInstitutionResourcePNMock().get(0);

        institutionResourcePN.setRootParent(new RootParentResourcePN());
        institutionResourcePN.getRootParent().setDescription("Parent Description");

        BffInstitution bffInstitution = InstitutionMapper.modelMapper.toBffInstitution(institutionResourcePN, pnBffConfigs);
        assertNotNull(bffInstitution);
        assertEquals(bffInstitution.getName(), institutionResourcePN.getDescription());
        assertEquals(bffInstitution.getProductRole(), institutionResourcePN.getUserProductRoles().get(0));
        assertEquals(bffInstitution.getParentName(), institutionResourcePN.getRootParent().getDescription());
        assertEquals(bffInstitution.getEntityUrl(), pnBffConfigs.getSelfcareBaseUrl() + "/token-exchange?institutionId=" + institutionResourcePN.getId() + "&productId=" + pnBffConfigs.getSelfcareSendProdId());
        assertNull(bffInstitution.getLogoUrl());

        BffInstitution bffInstitutionNull = InstitutionMapper.modelMapper.toBffInstitution(null, pnBffConfigs);
        assertNull(bffInstitutionNull);
    }
}