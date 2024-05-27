package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaSummary;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroup;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroupStatus;

import java.util.List;

public class RecipientInfoMock {

    public PgGroup getPgGroupMock(String id, String name, PgGroupStatus status) {
        PgGroup pgGroup = new PgGroup();
        pgGroup.setId(id);
        pgGroup.setName(name);
        pgGroup.setStatus(status);
        return pgGroup;
    }

    public List<PgGroup> getPgGroupsMock() {
        return List.of(
                getPgGroupMock("1", "Group 1", PgGroupStatus.ACTIVE),
                getPgGroupMock("2", "Group 2", PgGroupStatus.ACTIVE)
        );
    }

    public PaSummary getPaSummary() {
        PaSummary paSummary = new PaSummary();
        paSummary.setId("id-pa");
        paSummary.setName("Agenzia delle Entrate");

        return paSummary;
    }

    public List<PaSummary> getPaSummaryList() {
        PaSummary paSummary = new PaSummary();
        paSummary.setId("id-pa");
        paSummary.setName("Agenzia delle Entrate");

        PaSummary paSummary2 = new PaSummary();
        paSummary2.setId("id-roma");
        paSummary2.setName("Comune di Roma");

        PaSummary paSummary3 = new PaSummary();
        paSummary3.setId("id-milano");
        paSummary3.setName("Comune di Milano");

        return List.of(paSummary, paSummary2, paSummary3);
    }
}
