package it.pagopa.pn.bff.mappers.infopa;

import it.pagopa.pn.bff.config.PnBffConfigs;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import it.pagopa.pn.bff.mocks.PaInfoMock;
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
    private final PaInfoMock paInfoMock = new PaInfoMock();
    @Autowired
    private PnBffConfigs pnBffConfigs;

    @Test
    void testInstitutionProductMapper() {
        ProductResourcePN productResourcePN = paInfoMock.getProductResourcePNMock().get(0);

        BffInstitutionProduct bffInstitutionProduct = ProductMapper.modelMapper.toBffInstitutionProduct(productResourcePN, pnBffConfigs, new ProductMapperContext(UserMock.PN_CX_ID, UserMock.LANG));
        assertNotNull(bffInstitutionProduct);
        assertEquals(bffInstitutionProduct.getId(), productResourcePN.getId());
        assertEquals(bffInstitutionProduct.getTitle(), productResourcePN.getTitle());

        BffInstitutionProduct bffInstitutionProductNull = ProductMapper.modelMapper.toBffInstitutionProduct(null, pnBffConfigs, new ProductMapperContext(UserMock.PN_CX_ID, UserMock.LANG));
        assertNull(bffInstitutionProductNull);
    }
}