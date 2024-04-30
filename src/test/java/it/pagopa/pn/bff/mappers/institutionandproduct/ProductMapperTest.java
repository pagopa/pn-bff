package it.pagopa.pn.bff.mappers.institutionandproduct;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.mocks.InstitutionAndProductMock;
import it.pagopa.pn.bff.mocks.UserMock;
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
public class ProductMapperTest {
    private final InstitutionAndProductMock institutionAndProductMock = new InstitutionAndProductMock();
    @Autowired
    private PnBffConfigs pnBffConfigs;

    @Test
    void testInstitutionProductMapper() {
        ProductResourcePN productResourcePN = institutionAndProductMock.getProductResourcePNMock().get(0);

        BffInstitutionProduct bffInstitutionProduct = ProductMapper.modelMapper.toBffInstitutionProduct(productResourcePN, pnBffConfigs, UserMock.PN_CX_ID);
        assertNotNull(bffInstitutionProduct);
        assertEquals(bffInstitutionProduct.getId(), productResourcePN.getId());
        assertEquals(bffInstitutionProduct.getTitle(), productResourcePN.getTitle());

        BffInstitutionProduct bffInstitutionProductNull = ProductMapper.modelMapper.toBffInstitutionProduct(null, pnBffConfigs, UserMock.PN_CX_ID);
        assertNull(bffInstitutionProductNull);
    }
}