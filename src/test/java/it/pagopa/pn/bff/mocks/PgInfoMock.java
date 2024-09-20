package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroup;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PgGroupStatus;

import java.util.ArrayList;
import java.util.List;

public class PgInfoMock {
    private PgGroup getPgGroupMock(String id, String name, PgGroupStatus status) {
        PgGroup group = new PgGroup();
        group.setId(id);
        group.setName(name);
        group.setDescription("This is a group for test purpose");
        group.setStatus(status);

        return group;
    }

    public List<PgGroup> getPgGroupsMock() {
        List<PgGroup> pgGroups = new ArrayList<>();

        // first group
        pgGroups.add(getPgGroupMock("mock-id-1", "mock-group-1", PgGroupStatus.ACTIVE));
        // second group
        pgGroups.add(getPgGroupMock("mock-id-2", "mock-group-2", PgGroupStatus.ACTIVE));
        // third group
        pgGroups.add(getPgGroupMock("mock-id-3", "mock-group-3", PgGroupStatus.ACTIVE));

        return pgGroups;
    }
}
