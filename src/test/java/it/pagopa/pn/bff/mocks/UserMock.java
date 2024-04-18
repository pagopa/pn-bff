package it.pagopa.pn.bff.mocks;

import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroup;
import it.pagopa.pn.bff.generated.openapi.msclient.external_registries_selfcare.model.PaGroupStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserMock {

    public static final String PN_UID = "UID";
    public static final String PN_CX_ID = "CX_ID";
    public static final String SOURCECHANNEL = "WEB";
    public static final String SEARCH_CHANNEL = "SRCHANEL";
    public static final String SEARCH_DETAILS = "SRCDETAILS";
    public static final String INSTITUTION_ID = "INSTITUTION_ID";
    public static final List<String> PN_CX_GROUPS = Collections.singletonList("group");
    
    private PaGroup getPaGroupMock(String id, String name, PaGroupStatus status) {
        PaGroup group = new PaGroup();
        group.setId(id);
        group.setName(name);
        group.setDescription("This is a group for test purpose");
        group.setStatus(status);
        return group;
    }

    public List<PaGroup> getPaGroupsMock() {
        List<PaGroup> paGroups = new ArrayList<>();
        // first group
        paGroups.add(getPaGroupMock("mock-id-1", "mock-group-1", PaGroupStatus.ACTIVE));
        // second group
        paGroups.add(getPaGroupMock("mock-id-2", "mock-group-2", PaGroupStatus.ACTIVE));
        // third group
        paGroups.add(getPaGroupMock("mock-id-3", "mock-group-3", PaGroupStatus.ACTIVE));
        // fourth group
        paGroups.add(getPaGroupMock("mock-id-4", "mock-group-4", PaGroupStatus.ACTIVE));
        return paGroups;
    }
}