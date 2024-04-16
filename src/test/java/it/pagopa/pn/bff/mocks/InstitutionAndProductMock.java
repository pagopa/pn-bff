package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.server.v1.dto.BffInstitution;

import java.util.ArrayList;
import java.util.List;

public class InstitutionAndProductMock {
    private List<BffInstitution> bffInstitutions;

    public InstitutionAndProductMock() {
        this.bffInstitutions = new ArrayList<>();
        BffInstitution bffInstitutionOne = new BffInstitution();
        bffInstitutionOne.setId("1");
        bffInstitutionOne.setName("Institution One");
        bffInstitutionOne.setProductRole("admin");
        bffInstitutionOne.setLogoUrl("https://logo.com");
        bffInstitutionOne.setParentName("Parent One");
        bffInstitutionOne.setEntityUrl("https://entity.com");
        BffInstitution bffInstitutionTwo = new BffInstitution();
        bffInstitutionTwo.setId("2");
        bffInstitutionTwo.setName("Institution Two");
        bffInstitutionTwo.setProductRole("admin");
        bffInstitutionTwo.setLogoUrl("https://logo.com");
        bffInstitutionTwo.setParentName("Parent Two");
        bffInstitutionTwo.setEntityUrl("https://entity.com");
        this.bffInstitutions.add(bffInstitutionOne);
        this.bffInstitutions.add(bffInstitutionTwo);
    }
    public List<BffInstitution> getBffInstitutions() {
        return bffInstitutions;
    }
}
