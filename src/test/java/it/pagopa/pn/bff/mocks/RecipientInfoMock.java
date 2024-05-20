package it.pagopa.pn.bff.mocks;

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
}
