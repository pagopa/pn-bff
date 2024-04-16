package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.InstitutionResourcePN;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.ProductResourcePN;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;
import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitutionProduct;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class InstitutionAndProductMock {
    private List<BffInstitution> bffInstitutionsMock;
    private List<BffInstitutionProduct> bffInstitutionProductsMock;
    private List<InstitutionResourcePN> institutionResourcePNSMock;
    private List<ProductResourcePN> productResourcePNSMock;
    private final UUID institutionIdOne = UUID.randomUUID();
    private final UUID institutionIdTwo = UUID.randomUUID();

    public InstitutionAndProductMock() {
        initBffInstitution();
        initBffInstitutionProduct();
        initInstitutionResourcePN();
        initProductResourcePN();
    }

    private void initBffInstitution(){
        this.bffInstitutionsMock = new ArrayList<>();
        BffInstitution bffInstitutionOne = new BffInstitution();
        bffInstitutionOne.setId(institutionIdOne.toString());
        bffInstitutionOne.setName("Institution One");
        bffInstitutionOne.setProductRole("admin");
        bffInstitutionOne.setEntityUrl("https://fooselfcare.com/token-exchange?institutionId=" + institutionIdOne + "&productId=foo-prod-id");
        BffInstitution bffInstitutionTwo = new BffInstitution();
        bffInstitutionTwo.setId(institutionIdTwo.toString());
        bffInstitutionTwo.setName("Institution Two");
        bffInstitutionTwo.setProductRole("admin");
        bffInstitutionTwo.setEntityUrl("https://fooselfcare.com/token-exchange?institutionId=" + institutionIdTwo + "&productId=foo-prod-id");
        this.bffInstitutionsMock.add(bffInstitutionOne);
        this.bffInstitutionsMock.add(bffInstitutionTwo);
    }

    private void initBffInstitutionProduct(){
        this.bffInstitutionProductsMock = new ArrayList<>();
        BffInstitutionProduct bffInstitutionProductOne = new BffInstitutionProduct();
        bffInstitutionProductOne.setId("foo-one-dev-id");
        bffInstitutionProductOne.setTitle("Product One");
        bffInstitutionProductOne.setProductUrl("https://fooselfcare.com/token-exchange?institutionId=fooInstitutionId&productId=" + bffInstitutionProductOne.getId());
        BffInstitutionProduct bffInstitutionProductTwo = new BffInstitutionProduct();
        bffInstitutionProductTwo.setId("foo-two-dev-id");
        bffInstitutionProductTwo.setTitle("Product Two");
        bffInstitutionProductTwo.setProductUrl("https://fooselfcare.com/token-exchange?institutionId=fooInstitutionId&productId=" + bffInstitutionProductTwo.getId());
        this.bffInstitutionProductsMock.add(bffInstitutionProductOne);
        this.bffInstitutionProductsMock.add(bffInstitutionProductTwo);
    }

    private void initInstitutionResourcePN() {
        this.institutionResourcePNSMock = new ArrayList<>();
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
        this.institutionResourcePNSMock.add(institutionResourcePNOne);
        this.institutionResourcePNSMock.add(institutionResourcePNTwo);
    }

    private void initProductResourcePN() {
        this.productResourcePNSMock = new ArrayList<>();
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
        this.productResourcePNSMock.add(productResourcePNOne);
        this.productResourcePNSMock.add(productResourcePNTwo);
    }
}
