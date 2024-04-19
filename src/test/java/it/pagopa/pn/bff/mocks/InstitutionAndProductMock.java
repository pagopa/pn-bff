package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InstitutionAndProductMock {
    private static final UUID institutionIdOne = UUID.randomUUID();
    private static final UUID institutionIdTwo = UUID.randomUUID();

    public List<InstitutionResourcePN> getInstitutionResourcePNMock() {
        List<InstitutionResourcePN> institutionResourcePNSMock = new ArrayList<>();
        InstitutionResourcePN institutionResourcePNOne = new InstitutionResourcePN();
        institutionResourcePNOne.setAddress("Address One");
        institutionResourcePNOne.setDescription("Institution One");
        institutionResourcePNOne.setDigitalAddress("Digital Address One");
        institutionResourcePNOne.setExternalId("1");
        institutionResourcePNOne.setId(institutionIdOne);
        institutionResourcePNOne.setInstitutionType(InstitutionResourcePN.InstitutionTypeEnum.PA);
        institutionResourcePNOne.setZipCode("01010");
        institutionResourcePNOne.setTaxCode("12345678901");
        institutionResourcePNOne.setStatus("ACTIVE");
        List<String> userProductRoles = new ArrayList<>();
        userProductRoles.add("admin");
        institutionResourcePNOne.setUserProductRoles(userProductRoles);
        InstitutionResourcePN institutionResourcePNTwo = new InstitutionResourcePN();
        institutionResourcePNTwo.setAddress("Address Two");
        institutionResourcePNTwo.setDescription("Institution Two");
        institutionResourcePNTwo.setDigitalAddress("Digital Address Two");
        institutionResourcePNTwo.setExternalId("2");
        institutionResourcePNTwo.setId(institutionIdTwo);
        institutionResourcePNTwo.setInstitutionType(InstitutionResourcePN.InstitutionTypeEnum.PA);
        institutionResourcePNTwo.setZipCode("02020");
        institutionResourcePNTwo.setTaxCode("12345678902");
        institutionResourcePNTwo.setStatus("ACTIVE");
        institutionResourcePNTwo.setUserProductRoles(userProductRoles);
        institutionResourcePNSMock.add(institutionResourcePNOne);
        institutionResourcePNSMock.add(institutionResourcePNTwo);
        return institutionResourcePNSMock;
    }

    public List<ProductResourcePN> getProductResourcePNMock() {
        List<ProductResourcePN>productResourcePNSMock = new ArrayList<>();
        ProductResourcePN productResourcePNOne = new ProductResourcePN();
        productResourcePNOne.setDescription("Description One");
        productResourcePNOne.setId("foo-one-dev-id");
        productResourcePNOne.setLogo("https://logo.com");
        productResourcePNOne.setLogoBgColor("FFFFFF");
        productResourcePNOne.setIdentityTokenAudience("identityTokenAudience");
        productResourcePNOne.setTitle("Product One");
        productResourcePNOne.setUrlPublic("https://product.com");
        ProductResourcePN productResourcePNTwo = new ProductResourcePN();
        productResourcePNTwo.setDescription("Description Two");
        productResourcePNTwo.setId("foo-two-dev-id");
        productResourcePNTwo.setLogo("https://logo.com");
        productResourcePNTwo.setLogoBgColor("FFFFFF");
        productResourcePNTwo.setIdentityTokenAudience("identityTokenAudience");
        productResourcePNTwo.setTitle("Product Two");
        productResourcePNSMock.add(productResourcePNOne);
        productResourcePNSMock.add(productResourcePNTwo);
        return productResourcePNSMock;
    }
}
